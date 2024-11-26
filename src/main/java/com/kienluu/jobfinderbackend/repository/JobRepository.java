package com.kienluu.jobfinderbackend.repository;

import com.kienluu.jobfinderbackend.entity.JobEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
           "AND FUNCTION('MONTH', j.createdAt) = :month " +
           "AND FUNCTION('YEAR', j.createdAt) = :year")
    long countJobsByCompanyId(@Param("companyId") Long companyId,
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



    @Query("select count(p) from JobEntity p")
    Integer countAllJob();

    @Query("select count(p) from  JobEntity p where p.expireDate >= CURRENT_DATE ")
    Integer countJobNotExpired();

    @Query("select count(p) from  JobEntity p where p.field = :field and p.expireDate >= CURRENT_DATE ")
    Integer countAllByFieldAndNotExpried(@Param("field") String field);

    @Query("select count(j) from JobEntity j where j.company.id = :companyId" )
    Integer countJobByCompanyId(@Param("companyId") String companyId);

    @Query("select count(p) from CompanyEntity p join JobEntity j on p.id = j.company.id and p.id = :companyId and j.expireDate >= CURRENT_DATE " )
    Integer countJobNotExpireByCompanyId(@Param("companyId") String companyId);

    @Query("select job from ReportEntity rp join JobEntity job on rp.job.jobId = job.jobId where (select count(p) from ReportEntity p group by p.job.jobId) >=5")
    List<JobEntity> findReportedJobs();

    @Query("SELECT COUNT(j) FROM JobEntity j " +
            "WHERE FUNCTION('MONTH', j.createdAt) = :month " +
            "AND FUNCTION('YEAR', j.createdAt) = :year")
    long countJobsByMonthAndYear(@Param("month") int month,
                          @Param("year") int year);


    @Query("SELECT COUNT(j) FROM JobEntity j " +
            "WHERE j.field = :field " +
            "AND FUNCTION('MONTH', j.createdAt) = :month " +
            "AND FUNCTION('YEAR', j.createdAt) = :year")
    long countJobsByFieldAndMonthAndYear(@Param("field") String field,
                                         @Param("month") int month,
                                         @Param("year") int year);

    @Query("SELECT COUNT(j) FROM JobEntity j " +
            "WHERE FUNCTION('YEAR', j.createdAt) = :year")
    long countJobByYear(@Param("year") int year);
}
