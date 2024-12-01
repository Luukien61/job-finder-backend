package com.kienluu.jobfinderbackend.repository;

import com.kienluu.jobfinderbackend.entity.JobEntity;
import com.kienluu.jobfinderbackend.model.JobByField;
import com.kienluu.jobfinderbackend.model.JobState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<JobEntity, Long> {
    Optional<JobEntity> findByJobId(Long id);

    @Query("SELECT COUNT(j) FROM JobEntity j " +
           "WHERE j.company.id = :companyId " +
           "AND EXTRACT(MONTH FROM j.createdAt) = :month " +
           "AND EXTRACT(YEAR FROM j.createdAt) = :year")
    long countJobsByCompanyId(@Param("companyId") String companyId,
                              @Param("month") int month,
                              @Param("year") int year);

    @Query(value = "SELECT COUNT(*) FROM job " +
                   "WHERE company_id = :companyId " +
                   "AND EXTRACT(MONTH FROM created_at) = :month " +
                   "AND EXTRACT(YEAR FROM created_at) = :year",
            nativeQuery = true)
    long countJobsByCompanyIdInMonthNative(@Param("companyId") Long companyId,
                                           @Param("month") int month,
                                           @Param("year") int year);




    Page<JobEntity> findByCompanyId(@Param("companyId") String companyId, Pageable pageable);

    @Query("select job from JobEntity job where job.jobId= :jobId and job.expireDate >=CURRENT_DATE")
    Optional<JobEntity> findJobByidAndNotExpiry(@Param("jobId") Long jobId);

    @Query("select job from JobEntity job where job.expireDate>=CURRENT_DATE ")
    Page<JobEntity> getNewJobs(Pageable pageable);


    @Query("select count(p) from JobEntity p")
    Integer countAllJob();

    @Query("select count(p) from  JobEntity p where p.expireDate >= CURRENT_DATE ")
    Integer countJobNotExpired();

    @Query("select count(p) from  JobEntity p where p.field = :field and p.expireDate >= CURRENT_DATE ")
    Integer countAllByFieldAndNotExpried(@Param("field") String field);

    @Query("select count(j) from JobEntity j where j.company.id = :companyId")
    Integer countJobByCompanyId(@Param("companyId") String companyId);

    @Query("select count(p) from CompanyEntity p join JobEntity j on p.id = j.company.id and p.id = :companyId and j.expireDate >= CURRENT_DATE ")
    Integer countJobNotExpireByCompanyId(@Param("companyId") String companyId);

    //@Query("select job from ReportEntity rp join JobEntity job on rp.job.jobId = job.jobId where (select count(p) from ReportEntity p group by p.job.jobId) >=5")
    @Query("select job from ReportEntity rp join JobEntity job on rp.job.jobId = job.jobId WHERE job.state='PENDING'" +
            " group by job.jobId having count(rp) >= 5")
    List<JobEntity> findReportedJobs();

    @Query("SELECT COUNT(j) FROM JobEntity j " +
            "WHERE EXTRACT(MONTH FROM j.createdAt) = :month " +
            "AND EXTRACT(YEAR FROM j.createdAt) = :year")
    long countJobsByMonthAndYear(@Param("month") int month,
                                 @Param("year") int year);


    @Query("SELECT COUNT(j) FROM JobEntity j " +
            "WHERE j.field = :field " +
            "AND EXTRACT(MONTH FROM j.createdAt) = :month " +
            "AND EXTRACT(YEAR FROM j.createdAt) = :year")
    long countJobsByFieldAndMonthAndYear(@Param("field") String field,
                                         @Param("month") int month,
                                         @Param("year") int year);

    @Query("SELECT COUNT(j) FROM JobEntity j " +
            "WHERE EXTRACT(YEAR FROM j.createdAt) = :year")
    long countJobByYear(@Param("year") int year);

    @Query("SELECT j FROM JobEntity j WHERE j.company.id = :companyId")
    List<JobEntity> findAllByCompanyId(@Param("companyId") String companyId);

    @Modifying
    @Query("UPDATE JobEntity j SET j.state = :state WHERE j.company.id = :companyId")
    void banJobsByCompanyId(@Param("state") JobState state,
                            @Param("companyId") String companyId);

    @Query("select new com.kienluu.jobfinderbackend.model.JobByField(job.field, count(job.field)) " +
           "from JobEntity job where EXTRACT(MONTH FROM job.createdAt) = :month " +
           "AND EXTRACT(YEAR FROM job.createdAt) = :year and job.state!= 'BANNED' GROUP BY job.field")
    List<JobByField> getJobsByField(@Param("month") int month,
                                    @Param("year") int year);
}
