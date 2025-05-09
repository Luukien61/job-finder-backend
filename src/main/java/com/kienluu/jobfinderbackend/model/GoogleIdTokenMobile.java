package com.kienluu.jobfinderbackend.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GoogleIdTokenMobile {
    @JsonAlias("id_token")
    private String idToken;
}
