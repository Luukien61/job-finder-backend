package com.kienluu.jobfinderbackend.repository;

import com.kienluu.jobfinderbackend.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Long>{
    Optional<ReportEntity> findByStatus(ReportEntity.Status status);

}
