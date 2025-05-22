package com.kienluu.jobfinderbackend.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenResponse {
    private String access_token;
    private String token_type;
    private LocalDateTime expires_in;
    private String refresh_token;
}