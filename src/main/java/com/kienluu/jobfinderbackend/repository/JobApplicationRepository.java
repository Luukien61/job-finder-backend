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


    @Query("select app from JobApplicationEntity app where app.job.jobId= :jobId")
    List<JobApplicationEntity> findAllByJobId(Long jobId);

    @Query("SELECT count(app) FROM JobApplicationEntity app " +
            "WHERE EXTRACT(MONTH FROM app.createdDate) = :month " +
            "AND EXTRACT(YEAR FROM app.createdDate) = :year")
    Integer countAppsByMonth(@Param("month") int month,
                          @Param("year") int year);

    @Query("select count(*) from JobApplicationEntity applicant join JobEntity job on applicant.job.jobId= job.jobId " +
           "where job.company.id= :companyId " +
           "and EXTRACT(MONTH FROM applicant.createdDate) = :month " +
           "AND EXTRACT(YEAR FROM applicant.createdDate) = :year")
    Long countApplicantInMonthByCompany(@Param("companyId") String companyId,
                                        @Param("month") int month,
                                        @Param("year") int year);
}