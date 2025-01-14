package com.kienluu.jobfinderbackend.service;

import com.kienluu.jobfinderbackend.dto.JobApplicationDto;

import java.util.List;

public interface IJobApplicationService {
    void applyJob(JobApplicationDto job);
    JobApplicationDto getJobById(Long id);
    boolean isApplied(Long jobId, String userId);
    List<JobApplicationDto> getApplicationsByJobId(Long jobId);
    void acceptApplication(Long applicationId);
    void declineApplication(Long applicationId);
}
