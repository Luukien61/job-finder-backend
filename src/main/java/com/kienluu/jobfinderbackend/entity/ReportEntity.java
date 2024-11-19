package com.kienluu.jobfinderbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "report")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;
    private String userId;
    private Long jobId;
    private String companyId;
    @Column(name = "rpReason", columnDefinition = "TEXT", nullable = false)
    private String rpReason;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false,
    columnDefinition = "VARCHAR(255) DEFAULT 'Pending'")
    private Status status;

    public enum Status {
        Pending,
        Done
    }
}
