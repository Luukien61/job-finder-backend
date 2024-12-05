package com.kienluu.jobfinderbackend.elasticsearch.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompanyUpdateEvent  {
    private String companyId;
    private String companyName;
    private String logo;
}
