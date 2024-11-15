package com.kienluu.jobfinderbackend.service.implement;

import com.kienluu.jobfinderbackend.elasticsearch.event.EvenType;
import com.kienluu.jobfinderbackend.elasticsearch.event.JobChangedEvent;
import com.kienluu.jobfinderbackend.entity.JobEntity;
import com.kienluu.jobfinderbackend.repository.JobRepository;
import com.kienluu.jobfinderbackend.service.IJobService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class JobService implements IJobService {

    private final JobRepository jobRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public JobEntity saveJob(JobEntity jobEntity) {
        Optional<JobEntity> optionalJob = jobRepository.findByJobId(jobEntity.getJobId());
        if (optionalJob.isPresent()) {
            throw new RuntimeException("Job already exists");
        } else {
            JobEntity savedJob = jobRepository.save(jobEntity);
            eventPublisher.publishEvent(new JobChangedEvent(savedJob, EvenType.CREATED));
            return savedJob;
        }
    }

    @Override
    public JobEntity updateJob(JobEntity job) {
        JobEntity jobEntity = jobRepository.findByJobId(job.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found"));
        JobEntity savedJob = jobRepository.save(jobEntity);
        eventPublisher.publishEvent(new JobChangedEvent(savedJob, EvenType.UPDATED));
        return savedJob;
    }

    @Override
    public void deleteJob(JobEntity job) {
        JobEntity jobEntity = jobRepository.findByJobId(job.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found"));
        jobRepository.delete(jobEntity);
        eventPublisher.publishEvent(new JobChangedEvent(job, EvenType.DELETED));
    }

    public void deleteJobById(Long jobId) {
        JobEntity jobEntity = jobRepository.findByJobId(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        jobRepository.delete(jobEntity);
        eventPublisher.publishEvent(new JobChangedEvent(jobEntity, EvenType.DELETED));
    }
}
