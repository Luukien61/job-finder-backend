package com.kienluu.jobfinderbackend.security.jwt.provider;

import com.kienluu.jobfinderbackend.dto.response.TokenResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Map;

public interface IJWTProvider {
    String generateAccessToken(UserDetails userDetails);
    String generateRefreshToken(UserDetails userDetails);
    TokenResponse generateTokenResponse(UserDetails userDetails);
    TokenResponse generateTokenResponse(UserDetails userDetails, String refreshToken);
    String generateAccessToken(UserDetails userDetails, Map<String,Object> claims);
    String generateAccessToken(UserDetails userDetails, Collection<? extends GrantedAuthority> claims, String agent);
    String generateAccessToken(UserDetails userDetails, Collection<? extends GrantedAuthority> claims);
    boolean isTokenValid(String token,UserDetails userDetails);
    String exactUserName(String token);
    void inValidToken(String accessToken, String refreshToken);
}
