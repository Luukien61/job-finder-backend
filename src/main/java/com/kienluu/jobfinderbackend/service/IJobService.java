package com.kienluu.jobfinderbackend.service;

import com.kienluu.jobfinderbackend.dto.request.JobCreateRequest;
import com.kienluu.jobfinderbackend.dto.response.JobResponse;
import com.kienluu.jobfinderbackend.entity.JobEntity;

public interface IJobService {
    JobResponse saveJob(JobCreateRequest job);
    JobResponse updateJob(JobCreateRequest job);
    void deleteJob(JobEntity job);

}
