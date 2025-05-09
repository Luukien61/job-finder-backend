package com.kienluu.jobfinderbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebAuthnCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String credentialId;  // Base64Url encoded
    private String userHandle;    // Base64Url encoded
    private String publicKeyCose; // Base64Url encoded (public key COSE)
    private long signatureCount;  // Counter (sign count)
    private boolean isActive;     // Trạng thái của credential (active/inactive)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;
}
