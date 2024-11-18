package com.kienluu.jobfinderbackend.repository;

import com.kienluu.jobfinderbackend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByUserId(String id);
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<UserEntity> findByEmailAndPassword(String email, String password);

    Optional<Integer> countAllByUserId(String userId);

}
