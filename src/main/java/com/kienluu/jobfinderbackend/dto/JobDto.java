package com.kienluu.jobfinderbackend.dto;

import com.kienluu.jobfinderbackend.entity.JobEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link JobEntity}
 */
@Value
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobDto implements Serializable {
    Long jobId;
    String companyId;
    String companyName;
    String title;
    String location;
    String description;
    String requirements;
    String benefits;
    String workTime;
    String role;
    int minSalary;
    int maxSalary;
    int experience;
    int quantity;
    LocalDate createdAt;
    LocalDate updateAt;
    LocalDate expireDate;
    String gender;
    String type;
    String field;
}