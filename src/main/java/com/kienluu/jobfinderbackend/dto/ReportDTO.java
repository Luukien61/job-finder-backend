package com.kienluu.jobfinderbackend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    private Long jobId;
    private String companyId;
    private String rpReason;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String status;
}
