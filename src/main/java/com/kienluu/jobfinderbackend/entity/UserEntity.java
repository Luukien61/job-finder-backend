package com.kienluu.jobfinderbackend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.kienluu.jobfinderbackend.model.UserRole;
import com.kienluu.jobfinderbackend.util.AppUtil;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "user_entity")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity extends BaseUserEntity{

    @OneToMany(mappedBy = "user" , cascade = CascadeType.MERGE)
    @JsonBackReference
    List<ReportEntity> reports;

    @OneToMany(mappedBy = "user" )
    @JsonBackReference
    List<JobApplicationEntity> jobApplications;


    private String name;
    private String avatar;
    private String email;
    private String address;
    private String password;
    private String phone;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    @ManyToMany(fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<JobEntity> savedJobs;
    @ManyToMany(fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<JobEntity> appliedJobs;

    private String educationLevel;
    private String gender;

    private String university;
    private LocalDate dateOfBirth;


    @ElementCollection(targetClass = String.class, fetch = FetchType.LAZY)
    private List<String> searchHistory;
    @ElementCollection(targetClass = String.class, fetch = FetchType.LAZY)
    @Column(columnDefinition = "TEXT")
    private List<String> cv;

    @PrePersist
    public void generateUniqueId() {
        if (this.getId() == null) {
            this.setId("u_" + AppUtil.generateCustomUserId());
        }
    }

}
