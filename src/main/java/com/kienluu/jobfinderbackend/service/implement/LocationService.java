package com.kienluu.jobfinderbackend.service.implement;

import com.kienluu.jobfinderbackend.entity.JobEntity;
import com.kienluu.jobfinderbackend.model.Coordinates;
import com.kienluu.jobfinderbackend.repository.JobRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {

    private final GeocodingService geocodingService;
    private final JobRepository jobRepository;

    // Haversine formula để tính khoảng cách
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371; // Bán kính Trái Đất (km)

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

//    @PostConstruct
    public void init() {
        var jobs = jobRepository.findJobByLatitudeIsNull();
        log.info("Jobs found: {}", jobs.size());
        for (var job : jobs) {
            geocodeJobIfNeeded(job);
        }
    }


    public void geocodeJobIfNeeded(JobEntity job) {
        if (job.getLatitude() == null || job.getLongitude() == null) {
            Coordinates coords = geocodingService.geocodeAddressGG(job.getLocation() + "," + job.getProvince());
            log.info("Coordinates found: {}", coords);
            if (coords != null) {
                job.setLatitude(coords.getLatitude());
                job.setLongitude(coords.getLongitude());
                jobRepository.save(job);
            }
        }
    }
}
