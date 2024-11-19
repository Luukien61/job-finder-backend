package com.kienluu.jobfinderbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@Table(name = "company")
public class CompanyEntity {
    @Id
    private String companyId;
    private String name;
    private String logo;
    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "company_images", joinColumns = @JoinColumn(name = "company_company_id"))
    @Column(name = "image_url")
    private List<String> images;
    private String website;
    private String field;
    private String description;
    private String address;
    private String phone;
    private String email;
    @Column(columnDefinition = "INT DEFAULT 15")
    private int monthlyPost;

    @OneToMany(mappedBy = "company")
    private List<JobEntity> jobs;

}
