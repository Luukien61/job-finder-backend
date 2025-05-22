package com.kienluu.jobfinderbackend.security.jwt.algorithm;

import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum SecretAlgorithm {
    HmacSHA256(SignatureAlgorithm.HS256),
    EC(SignatureAlgorithm.ES256); //public and private key
    private SignatureAlgorithm algorithm;
}