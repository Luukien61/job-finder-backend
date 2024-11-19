package com.kienluu.jobfinderbackend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonManagedReference
    @JoinColumn(name = "company_id")
    private CompanyEntity company;
    private String title;
    private String location;
    private String description;
    private String requirements;
    private String benefits;
    private String workTime;
    private String role;
    private int minSalary;
    private int maxSalary;
    private int quantity;
    private int experience;
    private LocalDate updateAt;
    private LocalDate expireDate;
    private String gender;
    private String type; // part-time or full-time
    private String field;
}
