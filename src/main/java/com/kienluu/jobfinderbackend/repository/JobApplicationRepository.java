package com.kienluu.jobfinderbackend.repository;

import com.kienluu.jobfinderbackend.entity.JobApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplicationEntity, Long> {

}