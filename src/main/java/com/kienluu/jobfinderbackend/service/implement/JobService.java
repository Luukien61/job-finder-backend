package com.kienluu.jobfinderbackend.service.implement;

import com.kienluu.jobfinderbackend.dto.JobApplicationDto;
import com.kienluu.jobfinderbackend.dto.JobDto;
import com.kienluu.jobfinderbackend.dto.request.JobCreateRequest;
import com.kienluu.jobfinderbackend.dto.response.JobEmployerCard;
import com.kienluu.jobfinderbackend.elasticsearch.event.EvenType;
import com.kienluu.jobfinderbackend.elasticsearch.event.JobChangedEvent;
import com.kienluu.jobfinderbackend.entity.CompanyEntity;
import com.kienluu.jobfinderbackend.entity.JobApplicationEntity;
import com.kienluu.jobfinderbackend.entity.JobEntity;
import com.kienluu.jobfinderbackend.mapper.CustomMapper;
import com.kienluu.jobfinderbackend.model.JobState;
import com.kienluu.jobfinderbackend.repository.CompanyRepository;
import com.kienluu.jobfinderbackend.repository.JobRepository;
import com.kienluu.jobfinderbackend.service.IJobService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class JobService implements IJobService {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CustomMapper mapper;

    //eventPublisher.publishEvent(new JobChangedEvent(savedJob, EvenType.CREATED));


    @Override
    public JobDto saveJob(JobCreateRequest job) {
        CompanyEntity companyEntity = companyRepository.findCompanyById(job.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));
        JobEntity jobEntity = mapper.toJobEntity(job);
        jobEntity.setCompany(companyEntity);
        var now = LocalDate.now();
        jobEntity.setCreatedAt(now);
        jobEntity.setUpdateAt(now);
        jobEntity.setState(JobState.PENDING);
        jobEntity = jobRepository.save(jobEntity);
        eventPublisher.publishEvent(new JobChangedEvent(jobEntity, EvenType.CREATED));
        return mapper.toJobResponse(jobEntity);
    }

    @Override
    public JobDto updateJob(JobCreateRequest job) {
        return null;
    }

    @Override
    public void deleteJob(JobEntity job) {
        JobEntity jobEntity = jobRepository.findByJobId(job.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found"));
        jobRepository.delete(jobEntity);
        eventPublisher.publishEvent(new JobChangedEvent(job, EvenType.DELETED));
    }

    @Override
    public void deleteJobById(Long jobId) {
        JobEntity jobEntity = jobRepository.findByJobId(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        jobRepository.delete(jobEntity);
        eventPublisher.publishEvent(new JobChangedEvent(jobEntity, EvenType.DELETED));
    }

    @Override
    public JobDto getJobById(Long jobId) {
        JobEntity jobEntity = jobRepository.findByJobId(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        return mapper.toJobResponse(jobEntity);

    }

    @Override
    @Transactional
    public Page<JobEmployerCard> getJobCardsByCompanyId(String companyId, int page, int size) {
        Sort sort1= Sort.by(Sort.Order.desc("createdAt"), Sort.Order.asc("expireDate"));
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort1);
        Page<JobEntity> jobs = jobRepository.findByCompanyId(companyId, pageable);
        return jobs.map(this::toJobEmployerCard);
    }

    protected JobEmployerCard toJobEmployerCard(JobEntity jobEntity) {
        List<JobApplicationEntity> jobApplicationEntity = jobEntity.getApplications();
        List<JobApplicationDto> applicationDtos = jobApplicationEntity.stream().map(mapper::toJobApplicationDto).toList();
        return JobEmployerCard.builder()
                .jobId(jobEntity.getJobId())
                .title(jobEntity.getTitle())
                .state(jobEntity.getState())
                .expireDate(jobEntity.getExpireDate())
                .logo(jobEntity.getCompany().getLogo())
                .applications(applicationDtos)
                .build();
    }
}
