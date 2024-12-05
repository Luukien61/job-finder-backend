package com.kienluu.jobfinderbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class JobCardResponse {
    private Long jobId;

    private String title;

    private String province;

    private String companyName;

    private String companyId;

    private String logo;

    private int experience;

    private int minSalary;

    private int maxSalary;

    private LocalDate expireDate;

    private LocalDate createdAt;

    private String state;

}
