package com.kienluu.jobfinderbackend.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface JobWithDistanceProjection {
    Long getJobId();
    String getTitle();
    String getProvince();
    String getCompanyName();
    String getCompanyId();
    String getLogo();
    int getExperience();
    Integer getMinSalary();
    Integer getMaxSalary();
    LocalDate getExpireDate();
    LocalDate getCreatedAt();
    String getState();
    Double getLatitude();
    Double getLongitude();
    Double getDistance();
}
