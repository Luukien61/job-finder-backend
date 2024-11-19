package com.kienluu.jobfinderbackend.websocket.repository;

import com.kienluu.jobfinderbackend.websocket.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Page<ChatMessage> findChatMessageByConversationId(Long id, Pageable pageable);
    List<ChatMessage> findChatMessageByConversationIdOrderByTimestampDesc(Long id);
}
