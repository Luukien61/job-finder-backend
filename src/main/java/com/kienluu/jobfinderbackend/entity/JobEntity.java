package com.kienluu.jobfinderbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.Date;

@Entity
@Getter
@Table(name = "job")
public class JobEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String jobId;
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
    private Date updateAt;
    private Date expireDate;
    private String gender;
    private String type;
}
