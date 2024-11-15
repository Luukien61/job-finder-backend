package com.kienluu.jobfinderbackend.entity;

import com.kienluu.jobfinderbackend.model.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "user_entity")
public class UserEntity {
    @Id
    private String userId;
    private String name;
    private String avatar;
    private String email;
    private String address;
    private String password;
    private String phone;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    @ManyToMany
    private Set<JobEntity> savedJobs;
    @ManyToMany
    private Set<JobEntity> appliedJobs;

    private String university;
    private String location;
    private LocalDate dateOfBirth;

    @ManyToMany
    @JoinTable(name = "user_interest",
            joinColumns = @JoinColumn(referencedColumnName = "userId"),
            inverseJoinColumns = @JoinColumn(referencedColumnName = "id"))
    private Set<Field> interestingFields;


}
