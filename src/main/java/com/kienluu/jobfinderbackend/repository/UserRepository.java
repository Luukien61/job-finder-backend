package com.kienluu.jobfinderbackend.repository;

import com.kienluu.jobfinderbackend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findUserById(String id);
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<UserEntity> findByEmailAndPassword(String email, String password);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM user_entity_saved_jobs t where t.user_entity_id= :userId and t.saved_jobs_job_id= :jobId)", nativeQuery = true)
    boolean isJobSaved(@Param("userId") String userId,@Param("jobId") Long jobId);
}
