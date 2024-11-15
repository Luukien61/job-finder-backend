package com.kienluu.jobfinderbackend.websocket.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Participant {
    private String id;
    private String name;
    private String avatar;
}
