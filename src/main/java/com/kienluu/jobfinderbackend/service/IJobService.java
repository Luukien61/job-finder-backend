package com.kienluu.jobfinderbackend.service;

import com.kienluu.jobfinderbackend.dto.request.JobCreateRequest;
import com.kienluu.jobfinderbackend.dto.JobDto;
import com.kienluu.jobfinderbackend.dto.response.JobEmployerCard;
import com.kienluu.jobfinderbackend.entity.JobEntity;
import org.springframework.data.domain.Page;

public interface IJobService {
    JobDto saveJob(JobCreateRequest job);
    void updateJob(JobDto job);
    void deleteJob(JobEntity job);

    void deleteJobById(Long jobId);

    Page<JobEmployerCard> getJobCardsByCompanyId(String companyId, int page, int size);

    JobDto getJobById(Long jobId);

    Page<JobDto> getNewJobs(Integer page, Integer size);



}
