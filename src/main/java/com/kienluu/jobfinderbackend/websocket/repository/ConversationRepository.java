package com.kienluu.jobfinderbackend.websocket.repository;

import com.kienluu.jobfinderbackend.websocket.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("SELECT c FROM Conversation c WHERE :userId MEMBER OF c.users")
    List<Conversation> findConversationByUserId(@Param("userId") String userId);

    Conversation findConversationById(Long id);

    @Query("SELECT c FROM Conversation c WHERE :userId1 MEMBER OF c.users AND :userId2 MEMBER OF c.users")
    Optional<Conversation> findByTwoUsers(String userId1, String userId2);

}
