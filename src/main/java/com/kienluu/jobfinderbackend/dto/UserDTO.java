package com.kienluu.jobfinderbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDTO {
    private String userId;
    private String name;
    private String avatar;
    private String email;
    private String address;
    private String password;
    private String phone;
    private String university;
    private LocalDate dateOfBirth;
    private String gender;
    private String role;
}
