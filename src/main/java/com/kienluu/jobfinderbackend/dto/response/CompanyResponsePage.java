package com.kienluu.jobfinderbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CompanyResponsePage {
    private String name;
    private String address;
    private Long jobCount; // Số lượng công việc
    private String logo;
}
