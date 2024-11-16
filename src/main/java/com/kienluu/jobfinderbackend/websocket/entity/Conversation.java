package com.kienluu.jobfinderbackend.websocket.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "conversations")
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection(fetch = FetchType.EAGER, targetClass = String.class)
    private Set<String> users;

    private String lastMessage;

    @Column(columnDefinition = "varchar(10)")
    private String type;

    @Column(name = "created_at")
    private LocalDateTime modifiedAt = LocalDateTime.now();

}