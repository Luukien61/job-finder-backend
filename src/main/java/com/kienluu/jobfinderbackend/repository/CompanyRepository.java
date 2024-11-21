package com.kienluu.jobfinderbackend.repository;

import com.kienluu.jobfinderbackend.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, String> {
    Optional<CompanyEntity> findCompanyById(String id);
    boolean existsByEmail(String email);

    Optional<CompanyEntity> findByEmail(String email);
    Optional<CompanyEntity> findByName(String name);
    Optional<CompanyEntity> findCompanyEntityByEmailAndPassword(String email, String password);


}
