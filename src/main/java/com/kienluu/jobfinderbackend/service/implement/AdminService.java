package com.kienluu.jobfinderbackend.service.implement;

import com.kienluu.jobfinderbackend.dto.ReportDTO;
import com.kienluu.jobfinderbackend.entity.CompanyEntity;
import com.kienluu.jobfinderbackend.entity.ReportEntity;
import com.kienluu.jobfinderbackend.entity.UserEntity;
import com.kienluu.jobfinderbackend.model.ReportStatus;
import com.kienluu.jobfinderbackend.repository.CompanyRepository;
import com.kienluu.jobfinderbackend.repository.JobRepository;
import com.kienluu.jobfinderbackend.repository.ReportRepository;
import com.kienluu.jobfinderbackend.repository.UserRepository;
import com.kienluu.jobfinderbackend.service.IAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
//@AllArgsConstructor
@RequiredArgsConstructor
public class AdminService implements IAdminService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ReportRepository reportRepository;

    @Override
    public void inActiveCompany(String companyId) {
        CompanyEntity companyEntity = companyRepository.findByCompanyId(companyId).orElseThrow(
                ()-> new RuntimeException("Invalid company id"));
        companyEntity.setActiveState(false);
        companyRepository.save(companyEntity);
    }
//    @Override
//    public void delete(String companyId) {
//        CompanyEntity companyEntity = companyRepository.findByCompanyId(companyId).orElseThrow(
//                ()-> new RuntimeException("Invalid company id"));
//        companyRepository.delete(companyEntity);}


        @Override
    public void inActiveUser(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId).orElseThrow(()
                -> new RuntimeException("Invalid user id!"));
        userEntity.setActiveState(false);
        userRepository.save(userEntity);
    }

    @Override
    public int countAllUser() {
        return userRepository.countAllUser();
    }

    @Override
    public int countAllCompany() {
        return companyRepository.countAllCompany();
    }

    @Override
    public int countCompanyFindingEmployee() {
        return companyRepository.countCompanyByJobNotExpired();
    }

    @Override
    public int countAllJob() {
        return jobRepository.countAllJob();
    }

    @Override
    public int countJobNotExpired() {
        return jobRepository.countJobNotExpired();
    }

    @Override
    public int countJobNotExpiredByField(String field) {
        return jobRepository.countAllByFieldAndNotExpried(field);
    }

    @Override
    public int countJobByCompany(String companyId) {
        return jobRepository.countJobByCompanyId(companyId);
    }

    @Override
    public List<ReportEntity> findReportPending(String status) {
        return reportRepository.findByStatus(ReportStatus.fromString(status))  ;
    }
}
