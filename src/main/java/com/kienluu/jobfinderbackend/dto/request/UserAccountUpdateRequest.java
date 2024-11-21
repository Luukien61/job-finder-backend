package com.kienluu.jobfinderbackend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAccountUpdateRequest {
    private String id;
    private String oldPassword;
    private String newPassword;
    private String email;

}
