package com.kienluu.jobfinderbackend.repository;

import com.kienluu.jobfinderbackend.entity.AdminUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminUserEntityRepository extends JpaRepository<AdminUserEntity, String> {

    Optional<AdminUserEntity> findByEmailAndPassword(String email, String password);

    Optional<AdminUserEntity> findByEmail(String email);
}