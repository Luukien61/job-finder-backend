package com.kienluu.jobfinderbackend.websocket.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "conversations")
public class Conversation {
    @Id
    private String id;

    @Column(name = "user1_id")
    private String user1Id;

    @Column(name = "user2_id")
    private String user2Id;

    private String lastMessage;

    @Column(name = "modifief_at")
    private LocalDateTime modifiedAt = LocalDateTime.now();

}