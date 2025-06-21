package com.kienluu.jobfinderbackend.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserCreationRequest {
    private String email;
    private String password;
    private LocalDate createdAt;

}
