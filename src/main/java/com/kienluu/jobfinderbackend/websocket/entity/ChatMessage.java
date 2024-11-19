package com.kienluu.jobfinderbackend.websocket.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

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

}
