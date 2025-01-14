package com.kienluu.jobfinderbackend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.kienluu.jobfinderbackend.model.JobState;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

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
    @Column(columnDefinition = "TEXT")
    private String title;
    private String location;
    private String province;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(columnDefinition = "TEXT")
    private String requirements;
    @Column(columnDefinition = "TEXT")
    private String benefits;
    private String workTime;
    private String role;
    private int minSalary;
    private int maxSalary;
    private int quantity;
    private int experience;
    private LocalDate createdAt;
    private LocalDate updateAt;
    private LocalDate expireDate;
    private String gender;
    private String type; // part-time or full-time
    private String field;
    @Enumerated(EnumType.STRING)
    private JobState state;
    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.REMOVE)
    private List<JobApplicationEntity> applications;


    @OneToMany(mappedBy = "job", cascade = CascadeType.MERGE)
    @JsonBackReference
    List<ReportEntity> reports;



}
