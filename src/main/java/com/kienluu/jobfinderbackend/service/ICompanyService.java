package com.kienluu.jobfinderbackend.service;

import com.kienluu.jobfinderbackend.dto.CompanyDto;
import com.kienluu.jobfinderbackend.dto.request.UpdateCompanyRequest;
import com.kienluu.jobfinderbackend.dto.response.CompanyCreateResponse;
import com.kienluu.jobfinderbackend.dto.response.CompanyResponse;
import com.kienluu.jobfinderbackend.dto.response.LoginResponse;
import com.kienluu.jobfinderbackend.entity.CompanyEntity;
import com.kienluu.jobfinderbackend.model.CompanyJobDetailStatistics;
import com.kienluu.jobfinderbackend.model.CompanyMonthlyJob;
import com.kienluu.jobfinderbackend.model.CompanyStatistics;
import com.kienluu.jobfinderbackend.model.MailTemplate;
import org.springframework.data.domain.Page;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface ICompanyService {

    List<CompanyEntity> getCompanies();

    CompanyResponse getCompanyById(String id);

    CompanyResponse updateCompany(String companyId, UpdateCompanyRequest request);

    CompanyCreateResponse createCompany(CompanyDto request);

    LoginResponse login(String email, String password);

    String sendVerificationCode(MailTemplate mailTemplate) throws MessagingException, GeneralSecurityException, IOException;

    boolean canPostJob(String companyId);

    Long countNewApplicantsInMonth(String companyId, Integer month, Integer year);

    Long countJobsInMonth(String companyId, Integer month, Integer year);

    Long countApplicantsInMonth(String companyId, Integer month, Integer year);

    CompanyStatistics getCompanyStatistics(String companyId, Integer month, Integer year);

    List<CompanyMonthlyJob> getJobsIn12Month(String companyId, Integer month, Integer year);

    List<CompanyJobDetailStatistics> getJobDetailStatistics(String companyId);

    Boolean checkCompanyStatus(String companyId);

    Page<Object[]> getCompanies(int pageNumber, int pageSize, String sortBy, String sortOrder);
}
