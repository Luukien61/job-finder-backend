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

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        JobEntity jobEntity = JobEntity.builder()
                .jobId(0L)
                .companyId("1")
                .title("Tuyển kỹ sư Java Developer tại Hà Nội")
                .location("Hà Nội")
                .description("Tuyển kỹ sư Java SpringBoot")
                .requirements("Ít nhất 2 năm kinh nghiệm")
                .benefits("Lương 20 triệu")
                .workTime("8-17h")
                .role("Nhân viên")
                .minSalary(15)
                .maxSalary(20)
                .expireDate(LocalDate.parse("2024-11-11"))
                .experience(2)
                .updateAt(LocalDate.parse("2024-11-11"))
                .gender("Male")
                .type("Full time")
                .build();
        log.info(jobEntity.getExpireDate().toString());
    }
}
