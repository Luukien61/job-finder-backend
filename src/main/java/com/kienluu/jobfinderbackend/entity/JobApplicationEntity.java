package com.kienluu.jobfinderbackend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @JoinColumn(name = "user_id")
    @JsonManagedReference
    private UserEntity user;


    @ManyToOne
    @JoinColumn(name = "job_id")
    @JsonManagedReference
    private JobEntity job;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String cvUrl;

    private String state;

    @Column(columnDefinition = "TEXT")
    private String referenceLetter;
    private LocalDate createdDate;
}
