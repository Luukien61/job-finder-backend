package com.kienluu.jobfinderbackend.repository;

import com.kienluu.jobfinderbackend.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, String> {
    Optional<CompanyEntity> findByCompanyId(String id);
}
