package com.kienluu.jobfinderbackend.websocket.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConversationCreateRequest {
    private String senderId, recipientId, type, message;
    private Long id;
    @Builder.Default
    private LocalDateTime createdAt= LocalDateTime.now();
}
