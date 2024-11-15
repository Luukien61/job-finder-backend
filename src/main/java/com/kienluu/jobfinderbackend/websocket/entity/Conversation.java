//package com.kienluu.jobfinderbackend.websocket.entity;
//
//import com.fasterxml.jackson.annotation.JsonManagedReference;
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.time.LocalDateTime;
//import java.util.Set;
//
//@Entity
//@Getter
//@Setter
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@Table(name = "conversations")
//public class Conversation {
//    @Id
//    private String id;
//
//
//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(name = "conver_users",
//            joinColumns = @JoinColumn(name = "conversation_id", referencedColumnName = "id"),
//            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id")
//    )
//    @JsonManagedReference
//    private Set<User> users;
//
////    @Column(name = "user1_id")
////    private String user1Id;
////
////    @Column(name = "user2_id")
////    private String user2Id;
//
//    private String lastMessage;
//
//    @Column(columnDefinition = "varchar(10)")
//    private String type;
//
//    @Column(name = "created_at")
//    private LocalDateTime modifiedAt = LocalDateTime.now();
//
//}