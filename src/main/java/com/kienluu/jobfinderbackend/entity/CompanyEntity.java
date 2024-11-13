package com.kienluu.jobfinderbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Entity
@Getter
@Table(name = "company")
public class CompanyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String companyId;
    private String userId;
    private String name;
    private String logo;
    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "company_images", joinColumns = @JoinColumn(name = "company_companyId"))
    @Column(name = "image_url")
    private List<String> images;
    private String website;
    private String field;
    private String description;
    private String address;
    private String phone;
    private String email;
}
