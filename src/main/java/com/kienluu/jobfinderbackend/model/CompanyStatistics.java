package com.kienluu.jobfinderbackend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyStatistics {
    private Long newApplicants;
    private Long newJobs;
    private Long applicants;
    private List<CompanyMonthlyJob> monthlyJobs;
    private List<CompanyMonthlyApps> monthlyApps;
}
