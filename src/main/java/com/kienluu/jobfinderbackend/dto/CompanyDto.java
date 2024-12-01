package com.kienluu.jobfinderbackend.dto;

import com.kienluu.jobfinderbackend.model.CompanyState;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for {@link com.kienluu.jobfinderbackend.entity.CompanyEntity}
 */
@Value
@Builder
@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyDto implements Serializable {
    String id;
    String name;
    String logo;
    String wallpaper;
    String website;
    String field;
    String password;
    String description;
    String address;
    String phone;
    String email;
    CompanyState state;
    LocalDate createdAt;
}