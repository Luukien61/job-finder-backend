package com.kienluu.jobfinderbackend.websocket.repository;

import com.kienluu.jobfinderbackend.websocket.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, String> {

    List<Conversation> findConversationByUser1IdOrUser2Id(String user1Id, String user2Id);

    Conversation findConversationById(String id);
}
