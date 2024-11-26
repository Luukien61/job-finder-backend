package com.kienluu.jobfinderbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class JobApplicationDTO {
    private String id;
    private String userId;
    private String jobId;
    private String cvUrl;
    private String state;
    private String referenceLetter;
    private LocalDate createdDate;
}
