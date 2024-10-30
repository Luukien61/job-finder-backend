package com.kienluu.jobfinderbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "user")
public class CompanyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String companyId;
    private String name;
    private String logo;
    private String website;
    private String description;
    private String address;
    private String phone;
    private String email;
}
