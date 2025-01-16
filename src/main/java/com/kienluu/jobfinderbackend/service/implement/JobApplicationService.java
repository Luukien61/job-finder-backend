package com.kienluu.jobfinderbackend.service.implement;

import com.kienluu.jobfinderbackend.dto.JobApplicationDto;
import com.kienluu.jobfinderbackend.entity.JobApplicationEntity;
import com.kienluu.jobfinderbackend.entity.JobEntity;
import com.kienluu.jobfinderbackend.entity.UserEntity;
import com.kienluu.jobfinderbackend.entity.notification.AcceptNotification;
import com.kienluu.jobfinderbackend.mapper.CustomMapper;
import com.kienluu.jobfinderbackend.mapper.UserContext;
import com.kienluu.jobfinderbackend.model.JobApplicationState;
import com.kienluu.jobfinderbackend.model.NotificationStatus;
import com.kienluu.jobfinderbackend.repository.AcceptNotificationRepository;
import com.kienluu.jobfinderbackend.repository.JobApplicationRepository;
import com.kienluu.jobfinderbackend.repository.JobRepository;
import com.kienluu.jobfinderbackend.repository.UserRepository;
import com.kienluu.jobfinderbackend.service.IJobApplicationService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class JobApplicationService implements IJobApplicationService {
    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final CustomMapper mapper;
    private final ApplicationEventPublisher eventPublisher;
    private final AcceptNotificationRepository acceptNotificationRepository;

    @Override
    @Transactional
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
        jobEntity.getApplications().add(entity);
        jobApplicationRepository.save(entity);
        userRepository.save(user);
        jobRepository.save(jobEntity);
    }

    @Override
    public JobApplicationDto getJobById(Long id) {
        JobApplicationEntity entity = jobApplicationRepository.findById(id).orElseThrow(() -> new RuntimeException("Job not found"));
        UserContext userContext = new UserContext(entity.getUser());
        return mapper.toJobApplicationDto(entity, userContext);
    }

    @Override
    public boolean isApplied(Long jobId, String userId) {
        Optional<JobApplicationEntity> job = jobApplicationRepository.findByUserAndJob(userId, jobId);
        return job.isPresent();
    }

    @Override
    public List<JobApplicationDto> getApplicationsByJobId(Long jobId) {
        return jobApplicationRepository.findAllByJobId(jobId)
                .stream().map(
                        entity -> mapper.toJobApplicationDto(entity, new UserContext(entity.getUser()))).toList();
    }

    @Override
    @Transactional
    public void acceptApplication(Long applicationId) {
        JobApplicationEntity entity = findJobApplicationEntityById(applicationId);
        entity.setState(JobApplicationState.ACCEPTED);
        jobApplicationRepository.save(entity);

        String ACCEPT_MESSAGE = """
                Đơn ứng tuyển của bạn cho công việc ${title} đã được nhà tuyển dụng chấp nhận!
                Hãy chú ý email hoặc tin nhắn của bạn nhé!
                """;
        AcceptNotification notification = AcceptNotification.builder()
                .message(ACCEPT_MESSAGE)
                .title(entity.getJob().getTitle())
                .userId(entity.getUser().getId())
                .status(NotificationStatus.SENT)
                .createdAt(LocalDateTime.now())
                .build();
        acceptNotificationRepository.save(notification);
        eventPublisher.publishEvent(notification);
    }

    @Override
    public void declineApplication(Long applicationId) {
        JobApplicationEntity entity = findJobApplicationEntityById(applicationId);
        entity.setState(JobApplicationState.REJECTED);
        jobApplicationRepository.save(entity);
    }

    private JobApplicationEntity findJobApplicationEntityById(Long id) {
        return jobApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
    }
}
