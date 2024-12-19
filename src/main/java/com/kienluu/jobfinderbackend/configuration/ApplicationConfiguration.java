package com.kienluu.jobfinderbackend.configuration;

import com.kienluu.jobfinderbackend.entity.AdminUserEntity;
import com.kienluu.jobfinderbackend.model.UserRole;
import com.kienluu.jobfinderbackend.repository.AdminUserEntityRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.List;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfiguration {
    @Value("${app.cors.allowOrigins}")
    private List<String> frontEndUrl;

    @Value("${aws.access-key}")
    private String accessKey;

    @Value("${aws.secret-key}")
    private String secretKey;
    private final AdminUserEntityRepository adminUserEntityRepository;

    @Bean
    @Primary
    public CorsConfigurationSource corsConfigurationSource() {
        var configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);
        var urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", configuration);
        return urlBasedCorsConfigurationSource;
    }

    @Bean
    public HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        return restTemplate;
    }

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.AP_SOUTHEAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .build();
    }
    @PostConstruct
    public void initAdminAccount() {
        long all = adminUserEntityRepository.count();
        if(all==0) {
            AdminUserEntity adminUserEntity = new AdminUserEntity();
            adminUserEntity.setUsername("admin");
            adminUserEntity.setPassword("12345678");
            adminUserEntity.setEmail("admin@admin.com");
            adminUserEntity.setId(UUID.randomUUID().toString());
            adminUserEntity.setRole(UserRole.ADMIN);
            adminUserEntityRepository.save(adminUserEntity);
        }
    }
}
