package com.kienluu.jobfinderbackend.service;

import com.kienluu.jobfinderbackend.entity.JobEntity;

public interface IJobService {
    JobEntity saveJob(JobEntity job);
    JobEntity updateJob(JobEntity job);
    void deleteJob(JobEntity job);



}
