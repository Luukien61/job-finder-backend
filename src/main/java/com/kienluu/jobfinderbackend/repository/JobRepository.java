package com.kienluu.jobfinderbackend.repository;

import com.kienluu.jobfinderbackend.entity.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<JobEntity, Long> {
    Optional<JobEntity> findByJobId(Long id);

    @Query("SELECT COUNT(j) FROM JobEntity j WHERE j.company.companyId = :companyId " +
            "AND FUNCTION('MONTH', j.updateAt) = :month " +
            "AND FUNCTION('YEAR', j.updateAt) = :year")
    long countJobsByCompanyAndMonth(@Param("companyId") Long companyId,
                                    @Param("month") int month,
                                    @Param("year") int year);



}
