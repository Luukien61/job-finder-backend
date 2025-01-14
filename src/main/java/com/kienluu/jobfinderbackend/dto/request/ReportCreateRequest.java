package com.kienluu.jobfinderbackend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ReportCreateRequest {
    private String userId;
    private Long jobId;
    private String reason;
}
