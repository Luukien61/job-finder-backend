package com.kienluu.jobfinderbackend.dto;

import com.kienluu.jobfinderbackend.entity.JobApplicationEntity;
import com.kienluu.jobfinderbackend.model.JobApplicationState;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link JobApplicationEntity}
 */
@Value
@Builder
@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobApplicationDto implements Serializable {
    Long id;
    String userId;
    String userName;
    String userAvatar;
    Long jobId;
    String cvUrl;
    JobApplicationState state;
    String referenceLetter;
    LocalDate createdDate;
}