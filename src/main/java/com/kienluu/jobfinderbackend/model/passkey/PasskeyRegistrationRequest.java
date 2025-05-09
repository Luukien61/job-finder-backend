package com.kienluu.jobfinderbackend.model.passkey;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasskeyRegistrationRequest {
    @JsonProperty("id")
    private String credentialId;

    @JsonProperty("type")
    private String type;

    @JsonProperty("rawId")
    private String rawId;

    @JsonProperty("response")
    private ResponseData response;
}


