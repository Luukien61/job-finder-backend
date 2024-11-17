package com.kienluu.jobfinderbackend.websocket.service;

import com.kienluu.jobfinderbackend.repository.CompanyRepository;
import com.kienluu.jobfinderbackend.repository.UserRepository;
import com.kienluu.jobfinderbackend.websocket.entity.Conversation;
import com.kienluu.jobfinderbackend.websocket.model.ConversationCreateRequest;
import com.kienluu.jobfinderbackend.websocket.model.ConversationResponse;
import com.kienluu.jobfinderbackend.websocket.repository.ChatMessageRepository;
import com.kienluu.jobfinderbackend.websocket.repository.ConversationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.*;

@Service
@AllArgsConstructor
public class ConversationService {
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private ChatMessageRepository chatMessageRepository;


    public Conversation createConversation(ConversationCreateRequest request) {
        String senderId = request.getSenderId();
        String recipientId = request.getRecipientId();
        Optional<Conversation> existingConv = conversationRepository.findByTwoUsers(senderId,recipientId );
        if (existingConv.isPresent()) {
            throw new RuntimeException("Conversation between these users already exists");
        }
        companyRepository.findByCompanyId(senderId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        userRepository.findByUserId(recipientId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<String> users = new HashSet<>();
        users.add(senderId);
        users.add(recipientId);
        Date date = new Date();
        Conversation conversation = Conversation.builder()
                .type(request.getType())
                .lastMessage(request.getMessage())
                .users(users)
                .modifiedAt(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .build();
        conversation = conversationRepository.save(conversation);
        return conversation;

    }

    public List<ConversationResponse> findAllConversationsByUserId(String userId) {
        List<Conversation> conversations = conversationRepository.findConversationByUserId(userId);
        return conversations.stream().map(this::convert).toList();
    }

    public ConversationResponse findConversationByExactTwoUsers(String senderId, String recipientId) {
        Conversation conversations = conversationRepository.findByTwoUsers(senderId,recipientId)
                .orElseThrow(()->new RuntimeException("Conversation not found"));
        return convert(conversations);
    }


    private ConversationResponse convert(Conversation conversation) {
        List<String> userIds = conversation.getUsers().stream().toList();
        return ConversationResponse.builder()
                .id(conversation.getId())
                .type(conversation.getType())
                .lastMessage(conversation.getLastMessage())
                .modifiedAt(conversation.getModifiedAt())
                .userIds(userIds)
                .build();
    }
}
