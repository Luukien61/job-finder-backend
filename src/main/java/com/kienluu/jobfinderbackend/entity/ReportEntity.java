package com.kienluu.jobfinderbackend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.kienluu.jobfinderbackend.model.ReportStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "report")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonManagedReference
    UserEntity user;

    @ManyToOne
    @JoinColumn(name = "job_id")
    @JsonManagedReference
    JobEntity job;

    private String companyId;

    @Column(name = "reason", columnDefinition = "TEXT", nullable = false)
    private String rpReason;

    @Enumerated(EnumType.STRING)
    private ReportStatus status;


}
