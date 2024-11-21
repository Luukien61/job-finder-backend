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
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CompanyService implements ICompanyService {

    private CompanyRepository companyRepository;
    private final CustomMapper mapper;
    private final MailService mailService;

    private static final int MAX_POSTS_PER_MONTH = 5;

    @Override
    public List<CompanyEntity> getCompanies() {
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
    public LoginResponse login(String email, String password) {
        CompanyEntity companyEntity = companyRepository.findCompanyEntityByEmailAndPassword(email, password)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        return mapper.toLoginResponse(companyEntity);
    }
}
