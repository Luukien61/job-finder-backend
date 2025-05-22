package com.kienluu.jobfinderbackend.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerifyChallengeRequest {
    private String userId;
    private String challenge;
    private String signature;
}
