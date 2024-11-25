package com.kienluu.jobfinderbackend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class JobCreateRequest {
    private String companyId;
    private String title;
    private String location;
    private String description;
    private String requirements;
    private String benefits;
    private String workTime;
    private int quantity;
    private String role;
    private int minSalary;
    private int maxSalary;
    private int experience;
    private LocalDate updateAt;
    private LocalDate expireDate;
    private LocalDate createdAt;
    private String gender;
    private String type; // part-time or full-time
    private String field;

}
