package com.kienluu.jobfinderbackend.elasticsearch.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BanJobByCompanyEvent {
    private String companyId;
}
