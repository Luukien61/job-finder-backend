package com.kienluu.jobfinderbackend.websocket.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * DTO for {@link com.kienluu.jobfinderbackend.websocket.entity.ChatMessage}
 */
@Value
@Builder
@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessageDto implements Serializable {
    String id;
    String senderId;
    String recipientId;
    Long conversationId;
    String content;
    Date timestamp;
    String type;
}