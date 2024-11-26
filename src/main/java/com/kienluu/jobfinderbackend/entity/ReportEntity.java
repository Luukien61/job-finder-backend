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
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonManagedReference
    @JoinColumn(name = "job_id")
    private JobEntity job;

    private String companyId;

    @Column(name = "reason", columnDefinition = "TEXT", nullable = false)
    private String rpReason;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ReportStatus status = ReportStatus.PENDING;

}
