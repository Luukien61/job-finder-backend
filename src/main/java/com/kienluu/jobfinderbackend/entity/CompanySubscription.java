package com.kienluu.jobfinderbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "company_subscription")
public class CompanySubscription {
    @Id
    private String id;
    @OneToOne(fetch = FetchType.LAZY)
    private CompanyEntity company;
    private LocalDate startDate;
    private LocalDate endDate;
    @ManyToOne(fetch = FetchType.EAGER)
    private CompanyPlan plan;
    private String status;
    private String email;
}
