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

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM ReportEntity r WHERE r.job.jobId= :jobId AND r.userId = :userId")
    boolean existsByJobIdAndUserId(@Param("jobId") Long jobId, @Param("userId") String userId);

    @Query("select count(rp) from ReportEntity rp where rp.job.jobId = :jobId")
    Integer countByJobId(@Param("jobId") String jobId);

    @Query("select rp from ReportEntity rp where rp.job.jobId = :jobId")
    List<ReportEntity> findAllByJobId(@Param("jobId") String jobId);

}
