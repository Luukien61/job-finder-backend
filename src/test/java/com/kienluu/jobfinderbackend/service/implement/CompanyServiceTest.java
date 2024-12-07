package com.kienluu.jobfinderbackend.service.implement;

import com.kienluu.jobfinderbackend.entity.CompanyEntity;
import com.kienluu.jobfinderbackend.entity.CompanyPlan;
import com.kienluu.jobfinderbackend.entity.CompanySubscription;
import com.kienluu.jobfinderbackend.model.CompanyState;
import com.kienluu.jobfinderbackend.repository.CompanyRepository;
import com.kienluu.jobfinderbackend.repository.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.commons.util.StringUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-test.yml")
class CompanyServiceTest {

    @InjectMocks
    private CompanyService companyService;
    @Mock
    private CompanyRepository companyRepository;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private JobRepository jobRepository;



    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(companyService, "MONTHLY_POST", 15);
        ReflectionTestUtils.setField(companyService, "ULTIMATE_PLAN", "Ultimate");
        ReflectionTestUtils.setField(companyService, "PRO_PLAN", "Pro");
        ReflectionTestUtils.setField(companyService, "BASIC_PLAN", "Basic");
        ReflectionTestUtils.setField(companyService, "BASIC_PLAN_LIMIT", 30);
    }

    private CompanyEntity setupCompany(String tier) {
        if (StringUtils.isNotBlank(tier)) {
            CompanyPlan plan = CompanyPlan.builder()
                    .name(tier)
                    .build();
            CompanySubscription subscription = CompanySubscription.builder()
                    .plan(plan)
                    .build();
            return CompanyEntity.builder()
                    .state(CompanyState.ACTIVE)
                    .companySubscription(subscription)
                    .build();
        } else {
            return CompanyEntity.builder()
                    .state(CompanyState.ACTIVE)
                    .build();
        }
    }

    @Test
    void ultimate_can_post_job_return_true() {
        when(companyRepository.findCompanyById("company_123"))
                .thenReturn(Optional.of(setupCompany("Ultimate")));
        when(jobRepository.countJobsByCompanyId("company_123", 12, 2024))
                .thenReturn(31L);
        boolean canPostJob = companyService.canPostJob("company_123");
        assertTrue(canPostJob);
    }

    @Test
    void pro_can_post_job_return_true() {
        when(companyRepository.findCompanyById("company_123"))
                .thenReturn(Optional.of(setupCompany("Pro")));
        when(jobRepository.countJobsByCompanyId("company_123", 12, 2024))
                .thenReturn(31L);
        boolean canPostJob = companyService.canPostJob("company_123");
        assertTrue(canPostJob);
    }

    @Test
    void basic_can_post_job_return_false() {
        when(companyRepository.findCompanyById("company_123"))
                .thenReturn(Optional.of(setupCompany("Basic")));
        when(jobRepository.countJobsByCompanyId("company_123", 12, 2024))
                .thenReturn(31L);
        boolean canPostJob = companyService.canPostJob("company_123");
        assertFalse(canPostJob);
    }

    @Test
    void normal_can_post_job_return_false() {
        when(companyRepository.findCompanyById("company_123"))
                .thenReturn(Optional.of(setupCompany(null)));
        lenient().when(jobRepository.countJobsByCompanyId("company_123", 12, 2024))
                .thenReturn(16L);
        boolean canPostJob = companyService.canPostJob("company_123");
        assertFalse(canPostJob);
    }

    @Test
    void normal_can_post_job_return_true() {
        when(companyRepository.findCompanyById("company_123"))
                .thenReturn(Optional.of(setupCompany(null)));
        when(jobRepository.countJobsByCompanyId("company_123", 12, 2024))
                .thenReturn(14L);
        boolean canPostJob = companyService.canPostJob("company_123");
        assertTrue(canPostJob);
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    void tier_can_post_job(String tier, Long count, boolean expected) {
        when(companyRepository.findCompanyById("company_123"))
                .thenReturn(Optional.of(setupCompany(tier)));
        when(jobRepository.countJobsByCompanyId("company_123", 12, 2024))
                .thenReturn(count);
        boolean canPostJob = companyService.canPostJob("company_123");
        assertEquals(expected, canPostJob);
    }

    private static Stream<Arguments> provideTestCases() {
        return Stream.of(
                Arguments.of("Basic", 31L, false),
                Arguments.of("Basic", 15L, true),
                Arguments.of("Ultimate", 100L, true),
                Arguments.of("Pro", 50L, true),
                Arguments.of(null, 15L, false),
                Arguments.of(null, 14L, true)
        );
    }

}