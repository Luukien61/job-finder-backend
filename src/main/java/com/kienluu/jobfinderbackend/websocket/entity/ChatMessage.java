package com.kienluu.jobfinderbackend.websocket.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ChatMessage {
    @Id
    private String id;
    private String senderId;
    private String recipientId;
    private Long conversationId;
    @Column(columnDefinition = "TEXT")
    private String content;
    private Date timestamp;
    private String type;
    @Column(columnDefinition = "TEXT")
    private String caption;

}
