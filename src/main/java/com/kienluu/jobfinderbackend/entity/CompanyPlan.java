package com.kienluu.jobfinderbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "company_plan")
public class CompanyPlan {
    @Id
    private String id;
    private String name;
    private String description;
    private Long price;
    private String priceId;
    private String period;
    private String productId;
    private Integer priority;
    private Integer limitPost;
    @OneToMany
    @JsonIgnore
    private List<CompanySubscription> companySubscriptions;

}
