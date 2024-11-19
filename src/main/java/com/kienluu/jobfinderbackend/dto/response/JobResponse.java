package com.kienluu.jobfinderbackend.dto.response;

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
public class JobResponse implements Serializable {
    Long jobId;
    String companyCompanyId;
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
    LocalDate updateAt;
    LocalDate expireDate;
    String gender;
    String type;
    String field;
}