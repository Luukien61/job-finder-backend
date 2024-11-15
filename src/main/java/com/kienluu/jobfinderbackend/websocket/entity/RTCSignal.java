package com.kienluu.jobfinderbackend.websocket.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RTCSignal {
    private String type;
    private String targetUserId;
    private String senderUserId;
    private String senderName;
    private String senderAvatar;
    private Object payload;
}
