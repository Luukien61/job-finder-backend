package com.kienluu.jobfinderbackend.service.implement;

import com.kienluu.jobfinderbackend.dto.JobDto;
import com.kienluu.jobfinderbackend.dto.request.JobCreateRequest;
import com.kienluu.jobfinderbackend.dto.response.JobCardResponse;
import com.kienluu.jobfinderbackend.elasticsearch.event.EvenType;
import com.kienluu.jobfinderbackend.elasticsearch.event.JobChangedEvent;
import com.kienluu.jobfinderbackend.entity.CompanyEntity;
import com.kienluu.jobfinderbackend.entity.JobEntity;
import com.kienluu.jobfinderbackend.event.UserSearchEvent;
import com.kienluu.jobfinderbackend.mapper.CustomMapper;
import com.kienluu.jobfinderbackend.model.JobState;
import com.kienluu.jobfinderbackend.repository.CompanyRepository;
import com.kienluu.jobfinderbackend.repository.JobRepository;
import com.kienluu.jobfinderbackend.service.ICompanyService;
import com.kienluu.jobfinderbackend.service.IJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class JobService implements IJobService {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CustomMapper mapper;
    @Value("${app.monthly-post}")
    private int MONTHLY_POST;
    private final ICompanyService companyService;

    //eventPublisher.publishEvent(new JobChangedEvent(savedJob, EvenType.CREATED));


    @Override
    public JobDto saveJob(JobCreateRequest job) {
        CompanyEntity companyEntity = companyRepository.findCompanyById(job.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));
        boolean canPostJob = companyService.canPostJob(job.getCompanyId());
        if(canPostJob) {
            JobEntity jobEntity = mapper.toJobEntity(job);
            jobEntity.setCompany(companyEntity);
            jobEntity.setCreatedAt(job.getCreatedAt());
            jobEntity.setUpdateAt(job.getCreatedAt());
            jobEntity.setState(JobState.PENDING);
            jobEntity = jobRepository.save(jobEntity);
            eventPublisher.publishEvent(new JobChangedEvent(jobEntity, EvenType.CREATED));
            return mapper.toJobResponse(jobEntity);
        }else {
            throw new RuntimeException("Bạn đã vượt quá số bài đăng cho phép, nâng cấp tài khoản để tiếp tục đăng tin.");
        }
    }

    @Override
    public void updateJob(JobDto job) {
        JobEntity jobEntity = jobRepository.findByJobId(job.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found"));
        jobEntity.setTitle(job.getTitle());
        jobEntity.setDescription(job.getDescription());
        jobEntity.setRequirements(job.getRequirements());
        jobEntity.setBenefits(job.getBenefits());
        jobEntity.setWorkTime(job.getWorkTime());
        jobEntity.setLocation(job.getLocation());
        jobEntity.setProvince(job.getProvince());
        jobEntity.setMinSalary(job.getMinSalary());
        jobEntity.setMaxSalary(job.getMaxSalary());
        jobEntity.setRole(job.getRole());
        jobEntity.setQuantity(job.getQuantity());
        jobEntity.setType(job.getType());
        jobEntity.setExperience(job.getExperience());
        jobEntity.setGender(job.getGender());
        jobEntity.setField(job.getField());
        jobEntity.setExpireDate(job.getExpireDate());
        jobEntity.setUpdateAt(job.getUpdateAt());
        jobRepository.save(jobEntity);

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
    public Page<JobCardResponse> getJobCardsByCompany(String companyId, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<JobEntity> jobs = jobRepository.findByCompany(companyId, JobState.PENDING, pageable);
        return jobs.map(mapper::toJobCardResponse);
    }

    @Override
    public Page<JobDto> getNewJobs(Integer page, Integer size) {
        Sort sort = Sort.by(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(page, size,sort);
        Page<JobEntity> response = jobRepository.getNewJobs(pageable);
        return response.map(mapper::toJobResponse);
    }

    @Override
    public JobDto getJobByIdNotExpiryAndNotBan(Long jobId, String userId) {
        JobEntity job = jobRepository.findJobEntitiesByJobIdAndStateAndExpireDateGreaterThanEqual(jobId, JobState.PENDING, LocalDate.now());
        if(job==null) throw new RuntimeException("Job not found");
        if(userId!=null && !userId.isEmpty()){
            eventPublisher.publishEvent(new UserSearchEvent(userId,job.getTitle()));
        }
        return mapper.toJobResponse(job);
    }
}
