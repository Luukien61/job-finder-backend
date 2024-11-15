//package com.kienluu.jobfinderbackend.websocket.entity;
//
//import com.fasterxml.jackson.annotation.JsonBackReference;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.ManyToMany;
//import jakarta.persistence.Table;
//import lombok.*;
//
//import java.util.Set;
//
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
//@Entity
//@Table(name = "chat_user")
//public class User {
//    @Id
//    private String id;
//    private String userName;
//    private String password;
//    private String email;
//    private String phone;
//    private String avatar;
//    @ManyToMany(mappedBy = "users")
//    @JsonBackReference
//    private Set<Conversation> conversations;
//}
