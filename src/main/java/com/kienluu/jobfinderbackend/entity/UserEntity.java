package com.kienluu.jobfinderbackend.entity;

import com.kienluu.jobfinderbackend.model.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Setter
@Getter
@Table(name = "user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // kieu string khong tu gen duoc len de kieu long hoa, nhung
    private long userId;
    private String fullName;
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

}
