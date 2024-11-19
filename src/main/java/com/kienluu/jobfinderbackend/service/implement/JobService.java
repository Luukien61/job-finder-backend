package com.kienluu.jobfinderbackend.service.implement;

import com.kienluu.jobfinderbackend.dto.request.JobCreateRequest;
import com.kienluu.jobfinderbackend.dto.response.JobResponse;
import com.kienluu.jobfinderbackend.elasticsearch.event.EvenType;
import com.kienluu.jobfinderbackend.elasticsearch.event.JobChangedEvent;
import com.kienluu.jobfinderbackend.entity.CompanyEntity;
import com.kienluu.jobfinderbackend.entity.JobEntity;
import com.kienluu.jobfinderbackend.mapper.CustomMapper;
import com.kienluu.jobfinderbackend.repository.CompanyRepository;
import com.kienluu.jobfinderbackend.repository.JobRepository;
import com.kienluu.jobfinderbackend.service.IJobService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class JobService implements IJobService {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CustomMapper mapper;

    //eventPublisher.publishEvent(new JobChangedEvent(savedJob, EvenType.CREATED));


    @Override
    public JobResponse saveJob(JobCreateRequest job) {
        CompanyEntity companyEntity = companyRepository.findByCompanyId(job.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));
        JobEntity jobEntity = mapper.toJobEntity(job);
        jobEntity.setCompany(companyEntity);
        jobEntity = jobRepository.save(jobEntity);
        eventPublisher.publishEvent(new JobChangedEvent(jobEntity, EvenType.CREATED));
        return mapper.toJobResponse(jobEntity);
    }

    @Override
    public JobResponse updateJob(JobCreateRequest job) {
        return null;
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
