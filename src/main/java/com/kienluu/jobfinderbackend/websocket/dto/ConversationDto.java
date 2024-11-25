package com.kienluu.jobfinderbackend.websocket.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.kienluu.jobfinderbackend.websocket.entity.Conversation}
 */
@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationDto implements Serializable {
    Long id;
    String senderId;
    String receiverId;
    String lastMessage;
    String type;
    LocalDateTime modifiedAt;
}