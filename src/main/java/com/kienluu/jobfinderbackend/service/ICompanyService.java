package com.kienluu.jobfinderbackend.service;

import com.kienluu.jobfinderbackend.dto.request.UpdateCompanyRequest;
import com.kienluu.jobfinderbackend.dto.response.CompanyResponse;
import com.kienluu.jobfinderbackend.entity.CompanyEntity;

import java.util.List;

public interface ICompanyService {
//    public boolean canPostJob(String companyId);
//    void incrementMonthlyPost(String companyId);
//    void resetMonthlyPost(String companyId);

    List<CompanyEntity> getCompanies();

     CompanyResponse updateCompany(String companyId, UpdateCompanyRequest request);
}
