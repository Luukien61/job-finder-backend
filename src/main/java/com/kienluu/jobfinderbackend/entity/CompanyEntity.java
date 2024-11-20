package com.kienluu.jobfinderbackend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kienluu.jobfinderbackend.model.CompanyState;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;
import java.util.Set;

@Entity
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

    @OneToMany(mappedBy = "company")
    @JsonIgnore
    @JsonBackReference
    private Set<JobEntity> jobs;

    @Enumerated(EnumType.STRING)
    private CompanyState state;

}
