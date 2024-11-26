package com.kienluu.jobfinderbackend.service;

import com.kienluu.jobfinderbackend.dto.ReportDTO;
import com.kienluu.jobfinderbackend.entity.JobEntity;
import com.kienluu.jobfinderbackend.entity.ReportEntity;

import java.util.List;

public interface IAdminService {

        void inActiveCompany(String companyId);
        //    void delete(String companyId);
//        void inActiveUser(String userId);

        //    void deleteUser(String userId);
        int countAllUser();

        int countAllCompany();
        int countCompanyFindingEmployee();

        int countAllJob();
        int countJobNotExpired();
        int countJobNotExpiredByField(String field);
        int countJobByCompany(String companyId);
        int countJobNotExpiredByCompany(String companyId);
        List<ReportEntity> findReportPending(String status);


}
