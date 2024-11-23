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
public class CompanyResponse {
    private String id;
    private String address;
    private String description;
    private String name;
    private String logo;
    private String phone;
    private String wallpaper;
    private String website;
    private String email;
    private String field;
    private UserRole role;

}
