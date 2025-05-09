package com.kienluu.jobfinderbackend.model.passkey;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseData {
    @JsonProperty("clientDataJSON")
    private String clientDataJSON;

    @JsonProperty("attestationObject")
    private String attestationObject;

    @JsonProperty("authenticatorData")
    private String authenticatorData;

    @JsonProperty("publicKeyAlgorithm")
    private Integer publicKeyAlgorithm;

    @JsonProperty("publicKey")
    private String publicKey;

    private Set<String> transports;

}
