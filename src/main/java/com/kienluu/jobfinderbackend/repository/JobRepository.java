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

import java.time.LocalDate;
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

    @Query("select job from JobEntity job where job.company.id= :companyId and job.state= :state and job.expireDate >=CURRENT_DATE")
    Page<JobEntity> findByCompany(@Param("companyId") String companyId,@Param("state") JobState state, Pageable pageable);

    @Query("select job from JobEntity job where job.jobId= :jobId and job.expireDate >=CURRENT_DATE")
    Optional<JobEntity> findJobByidAndNotExpiry(@Param("jobId") Long jobId);

    @Query("select job from JobEntity job where job.expireDate>=CURRENT_DATE and job.state!= 'BANNED' ")
    Page<JobEntity> getNewJobs(Pageable pageable);


    @Query("select count(p) from JobEntity p")
    Integer countAllJob();

    @Query("select count(p) from  JobEntity p where p.expireDate >= CURRENT_DATE and p.state!='BANNED' ")
    Integer countJobNotExpired();

    @Query("select count(p) from  JobEntity p where p.field = :field and p.expireDate >= CURRENT_DATE ")
    Integer countAllByFieldAndNotExpried(@Param("field") String field);

    @Query("select count(j) from JobEntity j where j.company.id = :companyId")
    Integer countJobByCompanyId(@Param("companyId") String companyId);

    @Query("select count(p) from CompanyEntity p join JobEntity j on p.id = j.company.id and p.id = :companyId and j.expireDate >= CURRENT_DATE ")
    Integer countJobNotExpireByCompanyId(@Param("companyId") String companyId);

    //@Query("select job from ReportEntity rp join JobEntity job on rp.job.jobId = job.jobId where (select count(p) from ReportEntity p group by p.job.jobId) >=5")
    @Query("SELECT job.jobId, job.title, job.company.name, job.company.id, job.company.logo, count(rp.id) " +
            "FROM ReportEntity rp JOIN JobEntity job ON rp.job.jobId = job.jobId " +
            "WHERE rp.status= 'PENDING' " +
            "GROUP BY job.jobId, job.title, job.company.id, job.company.name, job.company.logo " +
            "HAVING count(rp.id) >= 5")
    List<Object[]> findReportedJobs();


    @Query("SELECT  job.jobId, job.title, count(rp.id), job.company.id, job.company.name " +
           "FROM ReportEntity rp JOIN JobEntity job ON rp.job.jobId = job.jobId " +
           "WHERE rp.status= 'PENDING'" +
           " GROUP BY job.jobId,job.company.name HAVING count(rp) >= 5")
    List<Object[]> findReportedJobsWithCount();

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

    @Query("SELECT EXTRACT(DAY FROM j.createdAt), COUNT(j) " +
            "FROM JobEntity j " +
            "WHERE EXTRACT(MONTH FROM j.createdAt) = :month " +
            "AND EXTRACT(YEAR FROM j.createdAt) = :year " +
            "GROUP BY EXTRACT(DAY FROM j.createdAt) " +
            "ORDER BY EXTRACT(DAY FROM j.createdAt)")
    List<Object[]> countJobsByDayInMonth(@Param("month") int month,
                                         @Param("year") int year);



    JobEntity findJobEntitiesByJobIdAndStateAndExpireDateGreaterThanEqual(Long jobId, JobState state, LocalDate date);
}
