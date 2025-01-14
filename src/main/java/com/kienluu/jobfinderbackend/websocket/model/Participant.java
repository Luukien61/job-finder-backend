package com.kienluu.jobfinderbackend.websocket.model;

import com.kienluu.jobfinderbackend.model.UserRole;
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
    private String email;
    private UserRole role;
}
