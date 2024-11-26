package com.kienluu.jobfinderbackend.dto.response;

import com.kienluu.jobfinderbackend.model.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ReportResponse {
    private Long id;
    private String userId;
    private Long jobId;
    private String reason;
    private ReportStatus status;

}
