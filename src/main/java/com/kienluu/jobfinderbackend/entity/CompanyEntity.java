package com.kienluu.jobfinderbackend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kienluu.jobfinderbackend.model.CompanyState;
import com.kienluu.jobfinderbackend.util.AppUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "company")
public class CompanyEntity extends BaseUserEntity {
    private String name;
    private String logo;
    private String wallpaper;
    private String website;
    private String field;

    private String password;
    private String description;
    private String address;
    private String phone;
    private String email;
    private LocalDate createdAt;

    @OneToMany(mappedBy = "company")
    @JsonIgnore
    @JsonBackReference
    private Set<JobEntity> jobs;

    @Enumerated(EnumType.STRING)
    private CompanyState state;

    @PrePersist
    public void generateUniqueId() {
        if (this.getId() == null) {
            this.setId("company_" + AppUtil.generateCustomUserId());
        }
    }
}
