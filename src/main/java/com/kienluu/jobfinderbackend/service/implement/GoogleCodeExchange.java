package com.kienluu.jobfinderbackend.service.implement;//package com.kienluu.jobfinderbackend.websocket.service;

import com.kienluu.jobfinderbackend.websocket.model.GoogleTokenResponse;
import com.kienluu.jobfinderbackend.websocket.model.GoogleUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleCodeExchange {

    private final RestTemplate restTemplate;
    private final HttpHeaders headers;


    @Value("${oauth.google.token-uri}")
    private String tokenUrl;
    @Value("${oauth.google.client-id}")
    private String clientId;
    @Value("${oauth.google.client-secret}")
    private String clientSecret;
    @Value("${oauth.google.grantType}")
    private String grantType;
    @Value("${oauth.google.redirect-uri}")
    private String redirectUri;

    private static final String GOOGLE_USER_INFO_URL = "https://www.googleapis.com/oauth2/v1/userinfo";

    public GoogleUserInfo exchange(String code) {
        GoogleTokenResponse tokenResponse = restTemplateExchange(code);
        String bearerToken = "Bearer " + tokenResponse.getAccessToken();
        return getUserInfo(bearerToken);

    }

    private GoogleUserInfo getUserInfo(String bearerToken) {
        // Tạo header với Authorization Bearer token
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, bearerToken);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<GoogleUserInfo> response = restTemplate.exchange(
                GOOGLE_USER_INFO_URL,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                GoogleUserInfo.class
        );

        return response.getBody();
    }

    private GoogleTokenResponse restTemplateExchange(String code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("grant_type", "authorization_code");
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, headers);
        ResponseEntity<GoogleTokenResponse> response = restTemplate.exchange(
                tokenUrl, HttpMethod.POST, httpEntity, GoogleTokenResponse.class);

        return response.getBody();
    }
}
