package com.kienluu.jobfinderbackend.respository;

import com.kienluu.jobfinderbackend.entity.UserEntity;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRespository extends JpaRepository<UserEntity, String> {

}
