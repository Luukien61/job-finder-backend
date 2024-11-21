package com.kienluu.jobfinderbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CompanyResponse {
    private String companyId;
    private String address;
    private String description;
    private String name;
    private String logo;
    private String website;
    private String email;
    private String field;

}
