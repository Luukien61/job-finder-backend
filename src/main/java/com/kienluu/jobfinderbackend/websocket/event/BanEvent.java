package com.kienluu.jobfinderbackend.websocket.event;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BanEvent {
    private String id;
    private String message;
    private String title;
    private String reason;
}
