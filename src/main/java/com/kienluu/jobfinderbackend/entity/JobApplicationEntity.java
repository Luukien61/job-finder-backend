package com.kienluu.jobfinderbackend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.kienluu.jobfinderbackend.model.JobApplicationState;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "job_application")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JobApplicationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonManagedReference
    private UserEntity user;


    @ManyToOne
    @JsonManagedReference
    private JobEntity job;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String cvUrl;

    @Enumerated(EnumType.STRING)
    private JobApplicationState state;

    @Column(columnDefinition = "TEXT")
    private String referenceLetter;
    private LocalDate createdDate;
}

