package com.kienluu.jobfinderbackend.service.implement;

import com.kienluu.jobfinderbackend.dto.CompanyDto;
import com.kienluu.jobfinderbackend.dto.request.UpdateCompanyRequest;
import com.kienluu.jobfinderbackend.dto.response.CompanyCreateResponse;
import com.kienluu.jobfinderbackend.dto.response.CompanyResponse;
import com.kienluu.jobfinderbackend.dto.response.LoginResponse;
import com.kienluu.jobfinderbackend.entity.CompanyEntity;
import com.kienluu.jobfinderbackend.mapper.CustomMapper;
import com.kienluu.jobfinderbackend.model.MailTemplate;
import com.kienluu.jobfinderbackend.model.UserRole;
import com.kienluu.jobfinderbackend.repository.CompanyRepository;
import com.kienluu.jobfinderbackend.service.ICompanyService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CompanyService implements ICompanyService {
    @Autowired
    private CompanyRepository companyRepository;
    private final CustomMapper mapper;
    private final MailService mailService;

    private static final int MAX_POSTS_PER_MONTH = 5;

    @Override
    public List<CompanyEntity> getCompanies() {

        return companyRepository.findAll();
    }

    @Override
    public List<CompanyEntity> getAllCompanies() {
        try {
            List<CompanyEntity> companies = companyRepository.findAll();
            System.out.println("Fetched companies: " + companies);
            return companies;
        } catch (Exception e) {
            System.err.println("Error fetching companies: " + e.getMessage());
            return Collections.emptyList();
        }
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
        company.setEmail(request.getEmail());
        company.setPhone(request.getPhone());
        company.setDescription(request.getDescription());
        company.setLogo(request.getLogo());
        company.setWebsite(request.getWebsite());
        company.setField(request.getField());

        company = companyRepository.save(company);

        return mapper.toCompanyResponse(company);
    }

    @Override
    public CompanyCreateResponse createCompany(CompanyDto request) {
        Optional<CompanyEntity> optionalCompany = companyRepository.findByEmail(request.getEmail());
        if(optionalCompany.isPresent()) {
            throw new RuntimeException("Company already exists");
        }
        CompanyEntity company = mapper.toCompanyEntity(request);
        company.setRole(UserRole.EMPLOYER);
        company = companyRepository.save(company);
        return mapper.toCompanyCreateResponse(company);
    }

    @Override
    public String sendVerificationCode(MailTemplate mailTemplate) throws MessagingException, GeneralSecurityException, IOException {
        return mailService.send(mailTemplate);
    }

    @Override
    public Page<Object[]> getCompanies(int page, int size, String sortBy, String sortOrder) {
        // Xác nhận các tham số sắp xếp hợp lệ
        List<String> validSortByFields = Arrays.asList("name", "address", "jobCount", "logo");
        if (!validSortByFields.contains(sortBy)) {
            throw new IllegalArgumentException("Invalid sortBy parameter: " + sortBy);
        }

        // Kiểm tra sortOrder hợp lệ
        if (sortOrder == null || (!sortOrder.equalsIgnoreCase("asc") && !sortOrder.equalsIgnoreCase("desc"))) {
            throw new IllegalArgumentException("Invalid sortOrder parameter: " + sortOrder);
        }

        // Tạo đối tượng Sort từ các tham số sắp xếp
        Sort sort;
        if ("jobCount".equalsIgnoreCase(sortBy)) {
            // Nếu muốn sắp xếp theo jobCount, bạn cần sửa trực tiếp trong câu truy vấn
            // Không thể sử dụng Pageable với jobCount vì đó là trường tính toán
            sort = Sort.by(Sort.Direction.fromString(sortOrder), "jobCount");
        } else {
            sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        }

        // Tạo Pageable từ các tham số phân trang
        Pageable pageable = PageRequest.of(page, size, sort);

        // Trả về kết quả phân trang từ repository
        return companyRepository.findAllByPage(pageable);
    }



    @Override
    public LoginResponse login(String email, String password) {
        CompanyEntity companyEntity = companyRepository.findCompanyEntityByEmailAndPassword(email, password)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        return mapper.toLoginResponse(companyEntity);
    }


}
