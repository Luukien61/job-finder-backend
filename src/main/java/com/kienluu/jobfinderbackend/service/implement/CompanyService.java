package com.kienluu.jobfinderbackend.service.implement;

import com.kienluu.jobfinderbackend.dto.request.UpdateCompanyRequest;
import com.kienluu.jobfinderbackend.dto.response.CompanyResponse;
import com.kienluu.jobfinderbackend.entity.CompanyEntity;
import com.kienluu.jobfinderbackend.repository.CompanyRepository;
import com.kienluu.jobfinderbackend.service.ICompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService implements ICompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    private static final int MAX_POSTS_PER_MONTH = 5;

    public List<CompanyEntity> getCompanies(){
        return companyRepository.findAll();
    }

    @Override
    public CompanyResponse updateCompany(String companyId, UpdateCompanyRequest request) {
        CompanyEntity company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        // Cập nhật thông tin
        company.setName(request.getName());
        company.setAddress(request.getAddress());
        company.setEmail(request.getEmail());
        company.setPhone(request.getPhone());
        company.setDescription(request.getDescription());
        company.setLogo(request.getLogo());
        company.setWebsite(request.getWebsite());
        company.setField(request.getField());

        // Lưu vào database
        company = companyRepository.save(company);

        // Chuyển đổi sang DTO CompanyResponse
        return CompanyResponse.builder()
                .companyId(company.getCompanyId())
                .address(company.getAddress())
                .description(company.getDescription())
                .name(company.getName())
                .logo(company.getLogo())
                .website(company.getWebsite())
                .email(company.getEmail())
                .field(company.getField())
                .build();
    }



//    @Override
//    public boolean canPostJob(String companyId) {
////        CompanyEntity company = companyRepository.findById(companyId)
////                .orElseThrow(() -> new RuntimeException("Company not found"));
////
////        // Kiểm tra số lượng bài đăng trong tháng
////        return company.getMonthlyPost() < MAX_POSTS_PER_MONTH;
//        return false;
//    }
//
//    @Override
//    public void incrementMonthlyPost(String companyId) {
////        CompanyEntity company = companyRepository.findById(companyId)
////                .orElseThrow(() -> new RuntimeException("Company not found"));
////
////        company.setMonthlyPost(company.getMonthlyPost() + 1);
////        companyRepository.save(company);
//    }
//
//    @Override
//    public void resetMonthlyPost(String companyId) {
////        CompanyEntity company = companyRepository.findById(companyId)
////                .orElseThrow(() -> new RuntimeException("Company not found"));
////
////        company.setMonthlyPost(0);
////        companyRepository.save(company);
//    }


}
