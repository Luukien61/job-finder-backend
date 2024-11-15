//package com.kienluu.jobfinderbackend.websocket.service;
//
//import com.kienluu.jobfinderbackend.websocket.entity.Conversation;
//import com.kienluu.jobfinderbackend.websocket.entity.User;
//import com.kienluu.jobfinderbackend.websocket.model.ConversationCreateRequest;
//import com.kienluu.jobfinderbackend.websocket.model.ConversationResponse;
//import com.kienluu.jobfinderbackend.websocket.repository.ChatMessageRepository;
//import com.kienluu.jobfinderbackend.websocket.repository.ConversationRepository;
//import com.kienluu.jobfinderbackend.websocket.repository.UserRepository;
//import lombok.AllArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.ZoneId;
//import java.util.*;
//
//@Service
//@AllArgsConstructor
//public class ConversationService {
//    private final ConversationRepository conversationRepository;
//    private final UserRepository userRepository;
//    private ChatMessageRepository chatMessageRepository;
//
//
//    public Conversation createConversation(ConversationCreateRequest request) {
//        String senderId = request.getSenderId();
//        String recipientId = request.getRecipientId();
//        List<Conversation> existingConv = conversationRepository.findConversationByExactTwoUsers(
//                Arrays.asList(senderId, recipientId)
//        );
//
//        if (!existingConv.isEmpty()) {
//            throw new RuntimeException("Conversation between these users already exists");
//        }
//
//        User user1 = userRepository.findById(senderId)
//                .orElseThrow(() -> new RuntimeException("User 1 not found with id: " + senderId));
//
//        User user2 = userRepository.findById(recipientId)
//                .orElseThrow(() -> new RuntimeException("User 2 not found with id: " + recipientId));
//
//
//        Set<User> users = new HashSet<>();
//        users.add(user1);
//        users.add(user2);
//        Date date = new Date();
//        Conversation conversation = Conversation.builder()
//                .type(request.getType())
//                .lastMessage(request.getMessage())
//                .users(users)
//                .modifiedAt(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
//                .id(request.getId())
//                .build();
//        conversation = conversationRepository.save(conversation);
//        // Lưu conversation
//        return conversation;
//
//    }
//
//    public List<ConversationResponse> findAllConversationsByUserId(String userId) {
//        List<Conversation> conversations = conversationRepository.findByUserId(userId);
//        return conversations.stream().map(this::convert).toList();
//    }
//
//    public ConversationResponse findConversationByExactTwoUsers(List<String> userIds) {
//        List<Conversation> conversations = conversationRepository.findConversationByExactTwoUsers(userIds);
//        if(!conversations.isEmpty()) {
//            Conversation conversation = conversations.get(0);
//            return convert(conversation);
//        }
//        else throw new RuntimeException("Conversation not found");
//    }
//
//
//    private ConversationResponse convert(Conversation conversation) {
//        List<String> userIds = conversation.getUsers().stream().map(User::getId).toList();
//        return ConversationResponse.builder()
//                .id(conversation.getId())
//                .type(conversation.getType())
//                .lastMessage(conversation.getLastMessage())
//                .modifiedAt(conversation.getModifiedAt())
//                .userIds(userIds)
//                .build();
//    }
//}
