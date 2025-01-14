package com.kienluu.jobfinderbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDTO {
    private String id;
    private String name;
    private String avatar;
    private String email;
    private String address;
    private String password;
    private String phone;
    private String educationLevel;
    private String university;
    private LocalDate dateOfBirth;
    private String gender;
    private String role;
    private List<String> cv;
    private Set<Long> savedJobs;
    private Set<Long> appliedJobs;
    private LocalDate createdAt;
    private List<String> searchHistory;
}
