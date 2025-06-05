package com.kienluu.jobfinderbackend.service.implement;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kienluu.jobfinderbackend.model.Coordinates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class GeocodingService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${geocoding.nominatim.url:https://nominatim.openstreetmap.org}")
    private String nominatimUrl;



    public Coordinates geocodeAddress(String address) {
        try {
            String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
            String url = nominatimUrl + "/search?q=" + encodedAddress + "&format=json&limit=1";

            Thread.sleep(1000);

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                    });

            List<Map<String, Object>> results = response.getBody();
            if (results != null && !results.isEmpty()) {
                Map<String, Object> result = results.get(0);
                double lat = Double.parseDouble(result.get("lat").toString());
                double lon = Double.parseDouble(result.get("lon").toString());
                return new Coordinates(lat, lon);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Value("${google.map-key}")
    private String GOOGLE_API_KEY;

    public Coordinates geocodeAddressGG(String address) {
        int maxRetries = 2;
        String currentAddress = address.trim();

        for (int retry = 0; retry <= maxRetries; retry++) {
            try {
                // Build URL with current address
                String url = UriComponentsBuilder.fromHttpUrl("https://maps.googleapis.com/maps/api/geocode/json")
                        .queryParam("address", currentAddress)
                        .queryParam("key", GOOGLE_API_KEY)
                        .queryParam("region", "vn")
                        .build()
                        .toUriString();

                log.info("Built URL (attempt {}): {}", retry + 1, url);

                HttpHeaders headers = new HttpHeaders();
                HttpEntity<String> entity = new HttpEntity<>(headers);

                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

                JsonNode root = objectMapper.readTree(response.getBody());
                String status = root.path("status").asText();
                if ("OK".equals(status)) {
                    JsonNode location = root.path("results").get(0).path("geometry").path("location");
                    double lat = location.path("lat").asDouble();
                    double lng = location.path("lng").asDouble();
                    // Check if coordinates are within Vietnam
                    if (lat >= 8 && lat <= 23 && lng >= 102 && lng <= 114) {
                        log.info("Valid coordinates found: lat={}, lng={}", lat, lng);
                        return new Coordinates(lat, lng);
                    } else {
                        log.warn("Invalid coordinates for Vietnam: lat={}, lng={}", lat, lng);
                    }
                } else {
                    String errorMessage = root.path("error_message").asText("Unknown error");
                    log.error("Geocoding failed with status: {}, message: {}", status, errorMessage);
                }

                // Prepare for retry by stripping address
                if (retry < maxRetries) {
                    int commaIndex = currentAddress.indexOf(",");
                    if (commaIndex != -1) {
                        currentAddress = currentAddress.substring(commaIndex + 1).trim();
                        log.info("Retrying with stripped address: {}", currentAddress);
                    } else {
                        log.warn("No comma found in address, cannot retry further: {}", currentAddress);
                        break;
                    }
                }
            } catch (Exception e) {
                log.error("Geocoding error: {}", e.getMessage());
                if (retry == maxRetries) {
                    return null;
                }
            }
        }
        log.error("Failed to geocode address after {} attempts: {}", maxRetries + 1, address);
        return null;
    }
}

