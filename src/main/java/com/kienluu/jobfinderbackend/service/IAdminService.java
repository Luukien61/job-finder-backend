package com.kienluu.jobfinderbackend.service;

import com.kienluu.jobfinderbackend.dto.JobDto;
import com.kienluu.jobfinderbackend.entity.JobEntity;
import com.kienluu.jobfinderbackend.entity.ReportEntity;
import com.kienluu.jobfinderbackend.model.ReportStatus;

import java.util.List;

public interface IAdminService {

        void deActivateCompany(String companyId);

        int countAllUser();

        int countAllCompany();
        int countCompanyFindingEmployee();

        int countAllJob();
        int countJobNotExpired();
        int countJobNotExpiredByField(String field);
        int countJobByCompany(String companyId);
        int countJobNotExpiredByCompany(String companyId);
        List<ReportEntity> findReportPending(String status);
        List<JobDto> reportedJobs();
        List<String> reportedDescription(Long jobId);
        long countJobsByFieldAndMonthAndyYear(String field, int month, int year);
        long countJobsByMonthAndYear(int month, int year);
        long countJobsByYear(int year);
        long countUserByYear(int year);
        long countCompanyByYear(int year);
        long countUserByMonthAndYear(int month, int year);
        long countCompanyByMonthAndYear(int month, int year);
        List<JobEntity> findJobsByCompanyId(String companyId);
        void rejectReports(ReportStatus status, long jobId);

        List<Long> countJobsByDayInMonth(int month, int year);
}

