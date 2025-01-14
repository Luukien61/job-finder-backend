package com.kienluu.jobfinderbackend.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public class AdminUserEntity extends BaseUserEntity {
    private String password;
    private String username;
    private String email;
}
