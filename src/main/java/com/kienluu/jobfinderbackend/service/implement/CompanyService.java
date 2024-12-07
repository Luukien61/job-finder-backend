package com.kienluu.jobfinderbackend.service.implement;

import com.kienluu.jobfinderbackend.dto.CompanyDto;
import com.kienluu.jobfinderbackend.dto.request.UpdateCompanyRequest;
import com.kienluu.jobfinderbackend.dto.response.CompanyCreateResponse;
import com.kienluu.jobfinderbackend.dto.response.CompanyResponse;
import com.kienluu.jobfinderbackend.dto.response.LoginResponse;
import com.kienluu.jobfinderbackend.elasticsearch.event.CompanyUpdateEvent;
import com.kienluu.jobfinderbackend.entity.CompanyEntity;
import com.kienluu.jobfinderbackend.entity.CompanyPlan;
import com.kienluu.jobfinderbackend.entity.CompanySubscription;
import com.kienluu.jobfinderbackend.mapper.CustomMapper;
import com.kienluu.jobfinderbackend.model.*;
import com.kienluu.jobfinderbackend.repository.CompanyRepository;
import com.kienluu.jobfinderbackend.repository.JobApplicationRepository;
import com.kienluu.jobfinderbackend.repository.JobRepository;
import com.kienluu.jobfinderbackend.service.ICompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyService implements ICompanyService {

    private final CompanyRepository companyRepository;
    private final CustomMapper mapper;
    private final MailService mailService;
    private final JobRepository jobRepository;
    private final JobApplicationRepository applicationRepository;
    private final ApplicationEventPublisher publisher;

    @Value("${app.monthly-post}")
    private int MONTHLY_POST;
    @Value("${stripe.checkout.plan.ultimate}")
    private String ULTIMATE_PLAN;
    @Value("${stripe.checkout.plan.pro}")
    private String PRO_PLAN;
    @Value("${stripe.checkout.plan.basic.name}")
    private String BASIC_PLAN;
    @Value("${stripe.checkout.plan.basic.limit}")
    private Integer BASIC_PLAN_LIMIT;

    @Override
    public List<CompanyEntity> getCompanies() {
        return companyRepository.findAll();
    }

    @Override
    public CompanyResponse getCompanyById(String id) {
        CompanyEntity companyEntity = companyRepository.findCompanyById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        return mapper.toCompanyResponse(companyEntity);
    }

    @Override
    public CompanyResponse updateCompany(String companyId, UpdateCompanyRequest request) {
        CompanyEntity company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        // Cập nhật thông tin
        company.setName(request.getName());
        company.setAddress(request.getAddress());
        company.setPhone(request.getPhone());
        company.setDescription(request.getDescription());
        company.setLogo(request.getLogo());
        company.setWebsite(request.getWebsite());
        company = companyRepository.save(company);
        publisher.publishEvent(new CompanyUpdateEvent(company.getId(), company.getName(), company.getLogo()));
        return mapper.toCompanyResponse(company);
    }

    @Override
    public CompanyCreateResponse createCompany(CompanyDto request) {
        Optional<CompanyEntity> optionalCompany = companyRepository.findByEmail(request.getEmail());
        if (optionalCompany.isPresent()) {
            throw new RuntimeException("Company already exists");
        }
        CompanyEntity company = mapper.toCompanyEntity(request);
        company.setRole(UserRole.EMPLOYER);
        company.setState(CompanyState.ACTIVE);
        company = companyRepository.save(company);
        return mapper.toCompanyCreateResponse(company);
    }

    @Override
    public String sendVerificationCode(MailTemplate mailTemplate) throws MessagingException, GeneralSecurityException, IOException {
        return mailService.send(mailTemplate);
    }

    @Override
    public LoginResponse login(String email, String password) {
        CompanyEntity companyEntity = companyRepository.findCompanyEntityByEmailAndPassword(email, password)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        return mapper.toLoginResponse(companyEntity);
    }

    @Override
    public boolean canPostJob(String companyId) {
        int limitJob = MONTHLY_POST;
        CompanyPlan plan = getCompanyPlan(companyId);
        if (plan != null) {
            if (ULTIMATE_PLAN.equals(plan.getName()) || PRO_PLAN.equals(plan.getName())) {
                return true;
            } else {
                limitJob = BASIC_PLAN_LIMIT;
            }
        }
        var now = LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();
        return jobRepository.countJobsByCompanyId(companyId, month, year) < limitJob;
    }

    @Override
    public Long countNewApplicantsInMonth(String companyId, Integer month, Integer year) {
        return companyRepository.countNewApplicantsInMonthByCompany(companyId, month, year);
    }

    @Override
    public Long countJobsInMonth(String companyId, Integer month, Integer year) {
        return companyRepository.countJobsInMonthByCompany(companyId, month, year);
    }

    @Override
    public Long countApplicantsInMonth(String companyId, Integer month, Integer year) {
        return applicationRepository.countApplicantInMonthByCompany(companyId, month, year);
    }

    @Override
    public CompanyStatistics getCompanyStatistics(String companyId, Integer month, Integer year) {
        Long newApplicantsInMonth = countNewApplicantsInMonth(companyId, month, year);
        Long newJobsInMonth = countJobsInMonth(companyId, month, year);
        Long applicantsInMonth = countApplicantsInMonth(companyId, month, year);
        List<CompanyMonthlyJob> monthlyJobs = getJobsIn12Month(companyId, month, year);
        return new CompanyStatistics(newApplicantsInMonth, newJobsInMonth, applicantsInMonth, monthlyJobs);
    }

    @Override
    public List<CompanyMonthlyJob> getJobsIn12Month(String companyId, Integer month, Integer year) {
        LocalDate startDate = LocalDate.of(year, month, 1).minusMonths(11); // Tính ngày bắt đầu
        List<CompanyMonthlyJob> statistics = companyRepository.countJobsInLast12Months(companyId, startDate);
        YearMonth current = YearMonth.of(year, month);
        List<CompanyMonthlyJob> finalResult = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            YearMonth finalCurrent = current;
            int currentYear = finalCurrent.getYear();
            int monthValue = finalCurrent.getMonthValue();
            CompanyMonthlyJob monthlyJob = statistics.stream()
                    .filter(stat -> stat.getYear() == currentYear && stat.getMonth() == monthValue)
                    .findFirst()
                    .orElse(new CompanyMonthlyJob(currentYear, monthValue, 0L));
            finalResult.add(monthlyJob);
            current = current.minusMonths(1);
        }
        return finalResult;
    }

    @Override
    public List<CompanyJobDetailStatistics> getJobDetailStatistics(String companyId) {
        return companyRepository.getJobsDetailStatisticByCompanyId(companyId);
    }

    @Override
    public Boolean checkCompanyStatus(String companyId) {
        CompanyState companyStatus = companyRepository.getCompanyStatus(companyId);
        return companyStatus == CompanyState.BAN;
    }

    private CompanyPlan getCompanyPlan(String companyId) {
        CompanyEntity companyEntity = companyRepository.findCompanyById(companyId.trim())
                .orElseThrow(() -> new RuntimeException("Company not found"));
        CompanySubscription companySubscription = companyEntity.getCompanySubscription();
        if (companySubscription == null) return null;
        return companySubscription.getPlan();
    }
}
