package com.kienluu.jobfinderbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReportDTO {
    private Long reportId;
    private String userId;
    private String jobId;
    private String companyId;
    private String rpReason;
    private String status;
}
