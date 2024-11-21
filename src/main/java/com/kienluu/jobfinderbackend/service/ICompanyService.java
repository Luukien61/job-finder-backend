package com.kienluu.jobfinderbackend.service;

import com.kienluu.jobfinderbackend.dto.CompanyDto;
import com.kienluu.jobfinderbackend.dto.request.UpdateCompanyRequest;
import com.kienluu.jobfinderbackend.dto.response.CompanyCreateResponse;
import com.kienluu.jobfinderbackend.dto.response.CompanyResponse;
import com.kienluu.jobfinderbackend.dto.response.LoginResponse;
import com.kienluu.jobfinderbackend.entity.CompanyEntity;
import com.kienluu.jobfinderbackend.model.MailTemplate;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface ICompanyService {

    List<CompanyEntity> getCompanies();

    CompanyResponse updateCompany(String companyId, UpdateCompanyRequest request);

    CompanyCreateResponse createCompany(CompanyDto request);

    LoginResponse login(String email, String password);

    String sendVerificationCode(MailTemplate mailTemplate) throws MessagingException, GeneralSecurityException, IOException;
}
