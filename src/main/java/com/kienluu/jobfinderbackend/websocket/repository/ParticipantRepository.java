package com.kienluu.jobfinderbackend.websocket.repository;

import com.kienluu.jobfinderbackend.entity.UserEntity;
import com.kienluu.jobfinderbackend.websocket.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, String> {
    Participant findParticipantById(String userId);
}
