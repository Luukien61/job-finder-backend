package com.kienluu.jobfinderbackend.service;

import com.kienluu.jobfinderbackend.dto.request.JobCreateRequest;
import com.kienluu.jobfinderbackend.dto.JobDto;
import com.kienluu.jobfinderbackend.dto.response.JobCardResponse;
import com.kienluu.jobfinderbackend.entity.JobEntity;
import org.springframework.data.domain.Page;

public interface IJobService {
    JobDto saveJob(JobCreateRequest job);
    void updateJob(JobDto job);
    void deleteJob(JobEntity job);

    void deleteJobById(Long jobId);

    Page<JobCardResponse> getJobCardsByCompany(String companyId, int page, int size);

    JobDto getJobById(Long jobId);

    JobDto getJobByIdNotExpiryAndNotBan(Long jobId, String userId);

    Page<JobDto> getNewJobs(Integer page, Integer size);



}
