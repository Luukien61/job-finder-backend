package com.kienluu.jobfinderbackend.websocket.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.kienluu.jobfinderbackend.entity.UserEntity;
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
    private String id;


    @ManyToMany(fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<UserEntity> users;

    private String lastMessage;

    @Column(columnDefinition = "varchar(10)")
    private String type;

    @Column(name = "created_at")
    private LocalDateTime modifiedAt = LocalDateTime.now();

}