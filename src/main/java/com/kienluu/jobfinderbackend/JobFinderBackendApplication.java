package com.kienluu.jobfinderbackend;

import com.kienluu.jobfinderbackend.entity.JobEntity;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.time.LocalDate;

@SpringBootApplication
@Slf4j
public class JobFinderBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobFinderBackendApplication.class, args);
    }

}
