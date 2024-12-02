package com.kienluu.jobfinderbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReportedJobDto {

    private Long jobId;
    private String title;
    private String companyName;
    private String companyId;
    private String companyLogo;
    private Long reportCount;
}
