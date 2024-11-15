package com.kienluu.jobfinderbackend.dto.request;

import com.kienluu.jobfinderbackend.model.UserRole;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCreationRequest {
    private String name;
    private String avatar;
    private String email;
    private String address;
    private String password;
    private String phone;
    @Enumerated(EnumType.STRING)
    private UserRole role;
}
