package com.kienluu.jobfinderbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Setter
@Getter
@Table(name = "job")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobId;
    private String companyId;
    private String title;
    private String location;
    private String description;
    private String requirements;
    private String benefits;
    private String workTime;
    private String role;
    private int minSalary;
    private int maxSalary;
    private int experience;
    private LocalDate updateAt;
    private LocalDate expireDate;
    private String gender;
    private String type; // part-time or full-time

    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "compay_id")
    private CompanyEntity company;
}
