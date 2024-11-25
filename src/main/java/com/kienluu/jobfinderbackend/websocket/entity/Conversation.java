package com.kienluu.jobfinderbackend.websocket.entity;

import com.kienluu.jobfinderbackend.entity.CompanyEntity;
import com.kienluu.jobfinderbackend.entity.UserEntity;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private CompanyEntity sender;

    @ManyToOne(fetch = FetchType.EAGER)
    private UserEntity receiver;

    private String lastMessage;

    @Column(columnDefinition = "varchar(10)")
    private String type;

    @Column(name = "created_at")
    private LocalDateTime modifiedAt = LocalDateTime.now();

}