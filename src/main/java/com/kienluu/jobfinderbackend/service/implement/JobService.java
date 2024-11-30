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
import com.kienluu.jobfinderbackend.mapper.UserContext;
import com.kienluu.jobfinderbackend.model.JobState;
import com.kienluu.jobfinderbackend.repository.CompanyRepository;
import com.kienluu.jobfinderbackend.repository.JobRepository;
import com.kienluu.jobfinderbackend.service.IJobService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService implements IJobService {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CustomMapper mapper;
    @Value("${app.monthly-post}")
    private int MONTHLY_POST;

    //eventPublisher.publishEvent(new JobChangedEvent(savedJob, EvenType.CREATED));


    @Override
    public JobDto saveJob(JobCreateRequest job) {
        CompanyEntity companyEntity = companyRepository.findCompanyById(job.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));
        var now = LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();
        long jobCount = jobRepository.countJobsByCompanyId(job.getCompanyId(), month, year);
        if(jobCount>MONTHLY_POST){
            throw new RuntimeException("Bạn đã sử dụng hết số bài đăng trong tháng");
        }
        JobEntity jobEntity = mapper.toJobEntity(job);
        jobEntity.setCompany(companyEntity);

        jobEntity.setCreatedAt(job.getCreatedAt());
        jobEntity.setUpdateAt(job.getCreatedAt());
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
        Sort sort1 = Sort.by(Sort.Order.desc("expireDate"),Sort.Order.desc("createdAt") );
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort1);
        Page<JobEntity> jobs = jobRepository.findByCompanyId(companyId, pageable);
        return jobs.map(this::toJobEmployerCard);
    }

    @Override
    public Page<JobDto> getNewJobs(Integer page, Integer size) {
        Sort sort = Sort.by(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(page, size,sort);
        Page<JobEntity> response = jobRepository.getNewJobs(pageable);
        return response.map(mapper::toJobResponse);
    }

    protected JobEmployerCard toJobEmployerCard(JobEntity jobEntity) {
        List<JobApplicationEntity> jobApplicationEntity = jobEntity.getApplications();
        List<JobApplicationDto> applicationDtos = jobApplicationEntity.stream()
                .map(item -> mapper.toJobApplicationDto(item, new UserContext(item.getUser())))
                .toList();
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
