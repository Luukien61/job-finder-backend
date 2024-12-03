package com.kienluu.jobfinderbackend.model;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CompanyJobDetailStatistics {
    private Long id;
    private String title;
    private LocalDate creatDate;
    private LocalDate expireDate;
    private int quantity;
    private Long applicants;
    private Long accepted;

}
