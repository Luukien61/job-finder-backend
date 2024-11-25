package com.kienluu.jobfinderbackend.repository;

import com.kienluu.jobfinderbackend.entity.ReportEntity;
import com.kienluu.jobfinderbackend.model.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Long>{
    List<ReportEntity> findByStatus(ReportStatus status);

    @Query("select count(rp) from ReportEntity rp where rp.job.jobId = :jobId")
    Integer countByJobId(@Param("jobId") String jobId);

    @Query("select rp from ReportEntity rp where rp.job.jobId = :jobId")
    List<ReportEntity> findAllByJobId(@Param("jobId") String jobId);

}
