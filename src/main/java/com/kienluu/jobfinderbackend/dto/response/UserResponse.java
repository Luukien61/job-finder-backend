package com.kienluu.jobfinderbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserResponse {
    private String userId;
    private String name;
    private String avatar;
    private String email;
    private String role;
}
