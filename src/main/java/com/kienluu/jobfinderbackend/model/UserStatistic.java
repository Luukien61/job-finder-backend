package com.kienluu.jobfinderbackend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatistic {
    private Long newMonthUsers;
    private Long lastMonthUsers;
    private Long totalUsers;
    private Long newCompanyUsers;
    private Long lastCompanyUsers;
    private Long totalCompanyUsers;
}
