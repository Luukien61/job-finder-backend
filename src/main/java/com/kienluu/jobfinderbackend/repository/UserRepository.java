package com.kienluu.jobfinderbackend.repository;

import com.kienluu.jobfinderbackend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    UserEntity findByUserId(String id);
}
