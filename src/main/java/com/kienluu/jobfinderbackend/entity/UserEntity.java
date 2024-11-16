package com.kienluu.jobfinderbackend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.kienluu.jobfinderbackend.model.UserRole;
import com.kienluu.jobfinderbackend.websocket.entity.Conversation;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "user_entity")
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    @ManyToMany(fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<JobEntity> savedJobs;
    @ManyToMany(fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<JobEntity> appliedJobs;

    private String university;
    private LocalDate dateOfBirth;

    @ElementCollection(targetClass = String.class, fetch = FetchType.LAZY)
    private List<String> interestingFields;

    @ManyToMany(mappedBy = "users")
    @JsonBackReference
    private Set<Conversation> conversations;
    @ElementCollection(targetClass = String.class, fetch = FetchType.LAZY)
    private List<String> searchHistory;

}
