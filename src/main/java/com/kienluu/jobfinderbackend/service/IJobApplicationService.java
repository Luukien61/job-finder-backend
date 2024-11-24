package com.kienluu.jobfinderbackend.service;

import com.kienluu.jobfinderbackend.dto.JobApplicationDto;

public interface IJobApplicationService {
    void applyJob(JobApplicationDto job);
    JobApplicationDto getJobById(Long id);
    boolean isApplied(Long jobId, String userId);
}
