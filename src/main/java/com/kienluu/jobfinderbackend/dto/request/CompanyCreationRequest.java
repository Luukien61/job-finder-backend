package com.kienluu.jobfinderbackend.dto.request;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyCreationRequest {
    private String name;
    private String logo;
    private String website;
    private String password;
    private String address;
    private String phone;
    private String email;

}
