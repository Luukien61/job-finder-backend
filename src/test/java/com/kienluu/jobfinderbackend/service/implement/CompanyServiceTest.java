package com.kienluu.jobfinderbackend.service.implement;

import com.kienluu.jobfinderbackend.entity.CompanyEntity;
import com.kienluu.jobfinderbackend.entity.CompanyPlan;
import com.kienluu.jobfinderbackend.entity.CompanySubscription;
import com.kienluu.jobfinderbackend.model.CompanyState;
import com.kienluu.jobfinderbackend.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @InjectMocks
    private CompanyService companyService;
    @Mock
    private CompanyRepository companyRepository;

    private CompanyEntity company;

    @BeforeEach
    void setUp() {
        CompanyPlan plan = CompanyPlan.builder()
                .name("Ultimate")
                .build();
        CompanySubscription subscription = CompanySubscription.builder()
                .plan(plan)
                .build();
        company = CompanyEntity.builder()
                .state(CompanyState.ACTIVE)
                .companySubscription(subscription)
                .build();
    }

    @Test
    void canPostJob() {
        when(companyRepository.findCompanyById("company_123"))
                .thenReturn(Optional.of(company));

    }
}