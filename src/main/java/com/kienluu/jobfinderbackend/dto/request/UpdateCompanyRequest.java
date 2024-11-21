package com.kienluu.jobfinderbackend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class UpdateCompanyRequest {
    private String name;
    private String email;
    private String phone;
    private String logo;
    private String address;
    private String description;
    private String website;
    private String field;
}
