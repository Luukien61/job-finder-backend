package com.kienluu.jobfinderbackend.dto;

import com.kienluu.jobfinderbackend.model.JobApplicationState;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class JobApplicationDto  {
    private Long id;
    private String userId;
    private String userName;
    private String userAvatar;
    private Long jobId;
    private String cvUrl;
    private JobApplicationState state;
    private String referenceLetter;
    //@DateTimeFormat(pattern = "yyyy/MM/dd") (1)
    private LocalDate createdDate;
}

/*
The default SpringBoot's date format is yyyy-MM-dd
 */