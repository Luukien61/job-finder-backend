package com.kienluu.jobfinderbackend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.kienluu.jobfinderbackend.model.UserRole;
import com.kienluu.jobfinderbackend.util.AppUtil;
import com.kienluu.jobfinderbackend.websocket.entity.Conversation;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.sql.ast.tree.expression.Collation;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
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

//    @OneToMany(mappedBy = "user" , cascade = CascadeType.MERGE)
//    @JsonBackReference
//    List<ReportEntity> reports;

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
    private LocalDate createdAt;

    @Column(columnDefinition = "varchar(255)")
    private String fcmToken;
    @Column(columnDefinition = "TEXT")
    private String publicBiometricKey;


    @ElementCollection(targetClass = String.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "user_entity_search_history", joinColumns = @JoinColumn(name = "user_entity_id"))
    @Column(name = "search_history", columnDefinition = "TEXT")
    private List<String> searchHistory;
    @ElementCollection(targetClass = String.class, fetch = FetchType.LAZY)
    @Column(columnDefinition = "TEXT")
    private List<String> cv;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true,mappedBy = "receiver", cascade = CascadeType.REMOVE)
    @JsonManagedReference
    private List<Conversation> conversations;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<WebAuthnCredential> credentials;

    @PrePersist
    public void generateUniqueId() {
        if (this.getId() == null) {
            this.setId("u_" + AppUtil.generateCustomUserId());
        }

    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_"+this.role.getRole()));
    }

}
