package com.kienluu.jobfinderbackend.dto.response;

import lombok.*;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ApiResponse<T> {
    private String status;
    private T data;
    private String message;
    private Map<String, String> errors;
    private Map<String, Object> metadata;


    public ApiResponse(String status, String message, Map<String, String> errors) {
        this.status = status;
        this.data = null;
        this.message = message;
        this.errors = errors;
        this.metadata = null;
    }
}
