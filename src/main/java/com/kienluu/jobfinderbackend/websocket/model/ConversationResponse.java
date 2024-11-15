package com.kienluu.jobfinderbackend.websocket.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ConversationResponse {
    private String id;

    private String lastMessage;

    private String type;

    private List<String> userIds;

    private LocalDateTime modifiedAt;
}
