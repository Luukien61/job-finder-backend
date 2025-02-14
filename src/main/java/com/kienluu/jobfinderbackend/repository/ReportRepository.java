package com.kienluu.jobfinderbackend.repository;

import com.kienluu.jobfinderbackend.entity.ReportEntity;
import com.kienluu.jobfinderbackend.model.ReportItemDetail;
import com.kienluu.jobfinderbackend.model.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Long> {
    List<ReportEntity> findByStatus(ReportStatus status);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM ReportEntity r WHERE r.job.jobId= :jobId AND r.userId = :userId")
    boolean existsByJobIdAndUserId(@Param("jobId") Long jobId, @Param("userId") String userId);

    @Query("select count(rp) from ReportEntity rp where rp.job.jobId = :jobId")
    Integer countByJobId(@Param("jobId") String jobId);

    @Query("select rp.rpReason from ReportEntity rp where rp.job.jobId = :jobId")
    List<String> findAllReportDescriptionByJobId(@Param("jobId") Long jobId);

    @Query(value = "select rp.id, u.email, u.avatar, rp.reason  " +
           "from report rp join user_entity u on rp.user_id=u.id " +
           "where rp.job_id = :jobId", nativeQuery = true)
    List<Object[]> findAllReportDetailByJobId(@Param("jobId") Long jobId);

    @Modifying
    @Query("UPDATE ReportEntity r SET r.status = :status WHERE r.companyId= :companyId")
    void updateReportStatusByCompanyId(@Param("status") ReportStatus status,
                                       @Param("companyId") String companyId);

    @Modifying
    @Query("UPDATE ReportEntity r SET r.status = :status WHERE r.job.jobId= :jobId")
    void updateReportStatusByJobId(@Param("status") ReportStatus status,
                                   @Param("jobId") Long jobId);


}
