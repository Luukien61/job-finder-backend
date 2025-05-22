package com.kienluu.jobfinderbackend.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PublicKeyRequest {
    private String userId;
    private String publicKey;
}
