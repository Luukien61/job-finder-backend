package com.kienluu.jobfinderbackend.service;

public interface ICompanyService {
    public boolean canPostJob(String companyId);
    void incrementMonthlyPost(String companyId);
    void resetMonthlyPost(String companyId);
}
