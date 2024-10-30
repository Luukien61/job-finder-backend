package com.kienluu.jobfinderbackend.entity;

import com.kienluu.jobfinderbackend.model.UserRole;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String userId;
    private String fullName;
    private String avatar;
    private String email;
    private String address;
    private String password;
    private String phone;
    @Enumerated(EnumType.STRING)
    private UserRole role;


}
