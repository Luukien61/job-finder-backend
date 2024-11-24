package com.kienluu.jobfinderbackend.service.implement;

import com.kienluu.jobfinderbackend.dto.JobApplicationDto;
import com.kienluu.jobfinderbackend.entity.JobApplicationEntity;
import com.kienluu.jobfinderbackend.entity.JobEntity;
import com.kienluu.jobfinderbackend.entity.UserEntity;
import com.kienluu.jobfinderbackend.mapper.CustomMapper;
import com.kienluu.jobfinderbackend.model.JobApplicationState;
import com.kienluu.jobfinderbackend.repository.JobApplicationRepository;
import com.kienluu.jobfinderbackend.repository.JobRepository;
import com.kienluu.jobfinderbackend.repository.UserRepository;
import com.kienluu.jobfinderbackend.service.IJobApplicationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class JobApplicationService implements IJobApplicationService {
    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final CustomMapper mapper;

    @Override
    public void applyJob(JobApplicationDto job) {
        Optional<JobApplicationEntity> optionalJobApplication = jobApplicationRepository.findByUserAndJob(job.getUserId(), job.getJobId());
        if (optionalJobApplication.isPresent()) {
            throw new RuntimeException("Bạn đã ứng tuyển công việc này");
        }
        UserEntity user = userRepository.findById(job.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        JobEntity jobEntity = jobRepository.findJobByidAndNotExpiry(job.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found"));
        JobApplicationEntity entity = mapper.toJobApplicationEntity(job);

        entity.setUser(user);
        entity.setJob(jobEntity);
        entity.setState(JobApplicationState.PENDING);
        Set<JobEntity> appliedJobs = user.getAppliedJobs();
        appliedJobs.add(jobEntity);
        user.setAppliedJobs(appliedJobs);
        jobApplicationRepository.save(entity);
        userRepository.save(user);
    }

    @Override
    public JobApplicationDto getJobById(Long id) {
        JobApplicationEntity entity = jobApplicationRepository.findById(id).orElseThrow(() -> new RuntimeException("Job not found"));
        return mapper.toJobApplicationDto(entity);
    }

    @Override
    public boolean isApplied(Long jobId, String userId) {
        Optional<JobApplicationEntity> job = jobApplicationRepository.findByUserAndJob(userId, jobId);
        return job.isPresent();
    }
}
