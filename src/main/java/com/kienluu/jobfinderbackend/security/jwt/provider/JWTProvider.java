package com.kienluu.jobfinderbackend.security.jwt.provider;

import com.kienluu.jobfinderbackend.dto.response.TokenResponse;
import com.kienluu.jobfinderbackend.security.jwt.algorithm.HS256AlgorithmProvider;
import com.kienluu.jobfinderbackend.security.jwt.algorithm.SecretAlgorithm;
import com.kienluu.jobfinderbackend.security.jwt.secret.BaseSecretProvider;
import com.kienluu.jobfinderbackend.security.jwt.secret.HS256Provider;
import com.kienluu.jobfinderbackend.util.AppConst;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JWTProvider implements IJWTProvider {
    private final SecretAlgorithm algorithm;
    private final Key secretKey;
    private long accessTokenExpire;
    private long refreshTokenExpire;
    private Date expireDate;

    @Value("${secret.accessTokenExpireTime}")
    private void getAccessTokenExpire(long time) {
        this.accessTokenExpire = time;
    }

    @Value("${secret.refreshTokenExpireTime}")
    private void getRefreshTokenExpire(long time) {
        this.refreshTokenExpire = time;
    }

    @Autowired
    public JWTProvider(
            HS256AlgorithmProvider hs256AlgorithmProvider,
            @Value("${secret.accessTokenExpireTime}") long expiredTime,
            @Value("${secret.refreshTokenExpireTime}") long refreshTime,
            @Value("${secret.key}") String key) {
        this.accessTokenExpire = expiredTime;
        this.refreshTokenExpire = refreshTime;
        BaseSecretProvider provider = new HS256Provider(key, hs256AlgorithmProvider);
        this.algorithm = provider.getProvider().getAlgorithm();
        this.secretKey = provider.getKey().get(0);
    }

    private String createToken(Map<String, Object> map, String subject, Long expiration) {
        expireDate = new Date(System.currentTimeMillis() + expiration * 1000);
        return Jwts.builder()
                //.setSubject(subject) (1)
                .setClaims(map)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setSubject(subject)
                .setExpiration(expireDate)
                .signWith(secretKey, algorithm.getAlgorithm())// if using ES256 , this secretKey will be private key
                .setHeaderParam("typ", "JWT")
                .compact();
    }

    @Override
    public String generateAccessToken(UserDetails userDetails) {
        return createToken(getClaims(userDetails), userDetails.getUsername(), this.accessTokenExpire);
    }

    @Override
    public String generateAccessToken(UserDetails userDetails, Map<String, Object> claims) {
        return createToken(claims, userDetails.getUsername(), this.accessTokenExpire);
    }

    @Override
    public String generateAccessToken(UserDetails userDetails, Collection<? extends GrantedAuthority> claims) {
        var authorities = new HashMap<String, Object>();
        var authorityNames = claims.stream().map(GrantedAuthority::getAuthority).toList();
        authorities.put("role", authorityNames);
        //authorities.put("sub",userDetails.getUsername());
        return createToken(authorities, userDetails.getUsername(), accessTokenExpire);
    }

    @Override
    public String generateAccessToken(UserDetails userDetails, Collection<? extends GrantedAuthority> claims, String agent) {
        var authorities = new HashMap<String, Object>();
        var authorityNames = claims.stream().map(GrantedAuthority::getAuthority).toList();
        authorities.put("role", authorityNames);
        authorities.put("agent", agent);
        //authorities.put("sub",userDetails.getUsername());
        return createToken(authorities, userDetails.getUsername(), accessTokenExpire);
    }

    @Override
    public TokenResponse generateTokenResponse(UserDetails userDetails) {

        String accessToken = generateAccessToken(userDetails);
        var localDate = expireDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        String refreshToken = generateRefreshToken(userDetails);
        return createTokenResponse(accessToken, refreshToken, localDate);
    }

    private TokenResponse createTokenResponse(
            String accessToken,
            String refreshToken,
            LocalDateTime expireTime
    ) {
        return TokenResponse.builder()
                .token_type(AppConst.BEARER)
                .access_token(accessToken)
                .refresh_token(refreshToken)
                .expires_in(expireTime)
                .build();
    }

    public TokenResponse generateTokenResponse(UserDetails userDetails, String refreshToken) {
        String accessToken = generateAccessToken(userDetails);
        var localDate = expireDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return createTokenResponse(accessToken, refreshToken, localDate);
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        return createToken(new HashMap<>(), userDetails.getUsername(), refreshTokenExpire);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        Date expireDate = exactClaim(token, Claims::getExpiration);
        var isNotExpiredToken = expireDate.after(new Date());
        if (isLogOutToken(token)) return false;
        return (userDetails.getUsername().equals(exactUserName(token)) && isNotExpiredToken);
    }

    @Override
    public String exactUserName(String token) {
        return exactClaim(token, Claims::getSubject);
    }

    private <T> T exactClaim(String token, Function<Claims, T> claimResolver) {
        Claims claims = exactAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims exactAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey) // and if using ES256 this key will be public key
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Map<String, Object> getClaims(UserDetails userDetails) {
        var authorities = new HashMap<String, Object>();
        var authorityNames = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        authorityNames = !authorityNames.isEmpty() ? authorityNames : new ArrayList<>();
        authorities.put("role", authorityNames);
        return authorities;
    }

    @Override
    public void inValidToken(String accessToken, String refreshToken) {

    }

    private boolean isLogOutToken(String token) {
        return false;
    }
}
