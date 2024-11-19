package com.kienluu.jobfinderbackend.repository;

import com.kienluu.jobfinderbackend.entity.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<JobEntity, Long> {
    Optional<JobEntity> findByJobId(Long id);

    @Query("SELECT COUNT(j) FROM JobEntity j " +
           "WHERE j.company.companyId = :companyId " +
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
}
