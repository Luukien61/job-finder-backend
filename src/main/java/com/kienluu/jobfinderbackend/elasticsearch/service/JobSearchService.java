package com.kienluu.jobfinderbackend.elasticsearch.service;

import com.kienluu.jobfinderbackend.elasticsearch.document.JobDocument;
import com.kienluu.jobfinderbackend.elasticsearch.event.EvenType;
import com.kienluu.jobfinderbackend.elasticsearch.event.JobChangedEvent;
import com.kienluu.jobfinderbackend.elasticsearch.repository.JobSearchRepository;
import com.kienluu.jobfinderbackend.entity.JobEntity;
import com.kienluu.jobfinderbackend.repository.JobRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class JobSearchService {
    private JobSearchRepository jobSearchRepository;
    private JobRepository jobRepository;

    @EventListener
    public void handleJobChange(JobChangedEvent event) {
        try {
            JobEntity job = event.getJob();
            EvenType action = event.getAction();
            String salary = job.getMinSalary() + " - " + job.getMaxSalary() + " triệu";
            switch (action) {
                case EvenType.CREATED:
                case EvenType.UPDATED:
                    JobDocument jobDocument = JobDocument.builder()
                            .title(job.getTitle())
                            .id(job.getJobId().toString())
                            .location(job.getLocation())
                            .companyId(job.getCompany().getId())
                            .companyName(job.getCompany().getName())
                            .logo(job.getCompany().getLogo())
                            .experience(job.getExperience())
                            .expiryDate(job.getExpireDate())
                            .salary(salary)
                            .build();
                    jobSearchRepository.save(jobDocument);
                    log.info("Elasticsearch synchronized for job ID: {} - Action: {}", job.getJobId(), action);
                    break;
                case EvenType.DELETED:
                    jobSearchRepository.deleteById(job.getJobId().toString());
                    break;
            }
        }catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    public void syncAllJobsToElasticsearch() {
        jobRepository.findAll().forEach(job -> {
            JobDocument jobDocument = new JobDocument();
            jobDocument.setId(job.getJobId().toString());
            jobDocument.setTitle(job.getTitle());
            jobDocument.setLocation(job.getLocation());
            jobSearchRepository.save(jobDocument);
        });
    }

    public Page<JobDocument> searchJobs(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return jobSearchRepository.findByTitleContainingOrLocationContaining(keyword,keyword,pageable);
    }



}
