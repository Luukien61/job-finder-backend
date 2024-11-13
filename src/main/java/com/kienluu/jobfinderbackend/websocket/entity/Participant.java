package com.kienluu.jobfinderbackend.websocket.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Participant {
    @Id
    private String id;
    private String name;
    private String avatar;
}