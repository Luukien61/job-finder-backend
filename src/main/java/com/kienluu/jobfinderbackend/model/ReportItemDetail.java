package com.kienluu.jobfinderbackend.model;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
public class ReportItemDetail {
    private Long id;
    private String email;
    private String avatar;
    private String description;
}
