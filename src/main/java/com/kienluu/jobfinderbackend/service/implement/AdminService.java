package com.kienluu.jobfinderbackend.service.implement;

import com.kienluu.jobfinderbackend.dto.JobDto;
import com.kienluu.jobfinderbackend.dto.ReportedJobDto;
import com.kienluu.jobfinderbackend.entity.CompanyEntity;
import com.kienluu.jobfinderbackend.entity.JobEntity;
import com.kienluu.jobfinderbackend.entity.ReportEntity;
import com.kienluu.jobfinderbackend.model.CompanyState;
import com.kienluu.jobfinderbackend.model.JobState;
import com.kienluu.jobfinderbackend.model.ReportStatus;
import com.kienluu.jobfinderbackend.repository.*;
import com.kienluu.jobfinderbackend.service.IAdminService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
//@AllArgsConstructor
@RequiredArgsConstructor
public class AdminService implements IAdminService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ReportRepository reportRepository;
    private final JobApplicationRepository jobApplicationRepository;

    @Override
    @Transactional
    public void deActivateCompany(String companyId) {
        CompanyEntity companyEntity = companyRepository.findById(companyId).orElseThrow(
                () -> new RuntimeException("Invalid company id"));
        companyEntity.setState(CompanyState.BAN);
        companyRepository.save(companyEntity);
//        List<JobEntity> jobs = jobRepository.findAllByCompanyId(companyId);
//        for (JobEntity job : jobs) {
//            job.setState(JobState.BANNED);
//        }
//        jobRepository.saveAll(jobs);

        jobRepository.banJobsByCompanyId(JobState.BANNED, companyId);
        reportRepository.updateReportStatusByCompanyId(ReportStatus.DONE, companyId);
    }

    public List<JobEntity> findJobsByCompanyId(String companyId) {
        return jobRepository.findAllByCompanyId(companyId);
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
    public int countJobNotExpiredByCompany(String companyId) {
        return jobRepository.countJobNotExpireByCompanyId(companyId);
    }

    @Override
    public List<ReportEntity> findReportPending(String status) {
        return reportRepository.findByStatus(ReportStatus.fromString(status));
    }

    @Override
    public List<ReportedJobDto> reportedJobs() {
        List<Object[]> results = jobRepository.findReportedJobs();

        return results.stream()
                .map(result -> new ReportedJobDto(
                        (Long) result[0],
                        (String) result[1],
                        (String) result[2],
                        (String) result[3],
                        (String) result[4],
                        (Long) result[5]
                ))
                .collect(Collectors.toList());

    }

    @Override
    public List<String> reportedDescription(Long jobId) {
        return reportRepository.findAllReportDescriptionByJobId(jobId);
    }

    @Override
    public long countJobsByFieldAndMonthAndyYear(String field, int month, int year) {
        return jobRepository.countJobsByFieldAndMonthAndYear(field, month, year);
    }

    @Override
    public long countJobsByMonthAndYear(int month, int year) {
        return jobRepository.countJobsByMonthAndYear(month, year);
    }

    @Override
    public long countJobsByYear(int year) {
        return jobRepository.countJobByYear(year);
    }

    @Override
    public long countUserByYear(int year) {
        return userRepository.countUserByYear(year);
    }

    @Override
    public long countCompanyByYear(int year) {
        return companyRepository.countCompanyByYear(year);
    }

    @Override
    public long countUserByMonthAndYear(int month, int year) {
        return userRepository.countUserByMonthAndYear(month, year);
    }

    @Override
    public long countCompanyByMonthAndYear(int month, int year) {
        return companyRepository.countCompanyByMonthAndYear(month, year);
    }


    @Override
    @Transactional
    public void rejectReports(ReportStatus status, long jobId) {
        reportRepository.updateReportStatusByJobId(status, jobId);
    }

    @Override
    public List<Long> countJobsByDayInMonth(int month, int year) {
        // Lấy kết quả từ database
        List<Object[]> results = jobRepository.countJobsByDayInMonth(month, year);

        // Số ngày trong tháng
        int daysInMonth = YearMonth.of(year, month).lengthOfMonth();
        int lastDay = (year == LocalDate.now().getYear() && month == LocalDate.now().getMonthValue())
                ? LocalDate.now().getDayOfMonth()
                : daysInMonth;

        // Tạo danh sách mặc định với tất cả các ngày, gán giá trị ban đầu là 0
        List<Long> jobCounts = new ArrayList<>(Collections.nCopies(lastDay, 0L));

        // Cập nhật số lượng công việc từ kết quả query vào danh sách
        for (Object[] result : results) {
            int day = (int) result[0];       // Ngày từ query
            long count = (long) result[1];  // Số lượng job từ query
            //if (day <= lastDay) { // Chỉ cập nhật cho các ngày <= ngày cuối cần xét
            jobCounts.set(day - 1, count);
            //}
        }

        return jobCounts;
    }

    //------------------------------job apply-------------------------------------

    @Override
    public int countAppsByMonth(int month, int year) {
        return jobApplicationRepository.countAppsByMonth(month, year);
    }
}
