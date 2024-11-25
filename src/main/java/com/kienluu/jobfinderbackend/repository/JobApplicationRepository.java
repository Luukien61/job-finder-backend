package com.kienluu.jobfinderbackend.repository;

import com.kienluu.jobfinderbackend.entity.JobApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplicationEntity, Long> {

    @Query("select app from JobApplicationEntity  app where app.job.jobId= :jobId and app.user.id= :userId")
    Optional<JobApplicationEntity> findByUserAndJob(@Param("userId") String userId,@Param("jobId") Long jobId);


}