package com.kienluu.jobfinderbackend.dto.response;

import com.kienluu.jobfinderbackend.dto.JobApplicationDto;
import com.kienluu.jobfinderbackend.model.JobState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class JobEmployerCard {
    private Long jobId;
    private String title;
    private LocalDate expireDate;
    private JobState state;
    private String logo;
    private List<JobApplicationDto> applications;
}
