package com.kienluu.jobfinderbackend.dto.response;

import com.kienluu.jobfinderbackend.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CompanyCreateResponse {
    private String id;
    private UserRole role;
}
