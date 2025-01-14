package com.kienluu.jobfinderbackend.dto.request;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyBanRequest {
    private String reason;
    private String title;
    private LocalDateTime createdAt;

}
