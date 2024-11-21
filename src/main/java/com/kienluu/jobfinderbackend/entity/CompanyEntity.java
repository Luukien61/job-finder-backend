package com.kienluu.jobfinderbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
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
    private Boolean activeState= true;

    @PrePersist
    public void setDefaultStates(){
        if(activeState==null){
            this.activeState=true;
        }
    }
}
