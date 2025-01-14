package com.kienluu.jobfinderbackend.dto;

import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.kienluu.jobfinderbackend.entity.CompanySubscription}
 */
@Value
public class CompanySubscriptionDto implements Serializable {
    String id;
    String companyId;
    LocalDate startDate;
    LocalDate endDate;
    String planId;
    String planName;
    String planPriceId;
    Integer planPriority;
    String status;
    String email;
}