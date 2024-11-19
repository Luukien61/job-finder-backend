package com.kienluu.jobfinderbackend.service.implement;

import com.kienluu.jobfinderbackend.entity.CompanyEntity;
import com.kienluu.jobfinderbackend.repository.CompanyRepository;
import com.kienluu.jobfinderbackend.service.ICompanyService;
import org.springframework.stereotype.Service;

@Service
public class CompanyService implements ICompanyService {

    private CompanyRepository companyRepository;
    private static final int MAX_POSTS_PER_MONTH = 5;
    @Override
    public boolean canPostJob(String companyId) {
//        CompanyEntity company = companyRepository.findById(companyId)
//                .orElseThrow(() -> new RuntimeException("Company not found"));
//
//        // Kiểm tra số lượng bài đăng trong tháng
//        return company.getMonthlyPost() < MAX_POSTS_PER_MONTH;
        return false;
    }

    @Override
    public void incrementMonthlyPost(String companyId) {
//        CompanyEntity company = companyRepository.findById(companyId)
//                .orElseThrow(() -> new RuntimeException("Company not found"));
//
//        company.setMonthlyPost(company.getMonthlyPost() + 1);
//        companyRepository.save(company);
    }

    @Override
    public void resetMonthlyPost(String companyId) {
//        CompanyEntity company = companyRepository.findById(companyId)
//                .orElseThrow(() -> new RuntimeException("Company not found"));
//
//        company.setMonthlyPost(0);
//        companyRepository.save(company);
    }


}
