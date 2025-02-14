package com.kienluu.jobfinderbackend.websocket.service;

import com.kienluu.jobfinderbackend.entity.CompanyEntity;
import com.kienluu.jobfinderbackend.entity.UserEntity;
import com.kienluu.jobfinderbackend.repository.CompanyRepository;
import com.kienluu.jobfinderbackend.repository.UserRepository;
import com.kienluu.jobfinderbackend.websocket.dto.ChatMessageDto;
import com.kienluu.jobfinderbackend.websocket.dto.ConversationDto;
import com.kienluu.jobfinderbackend.websocket.entity.ChatMessage;
import com.kienluu.jobfinderbackend.websocket.entity.Conversation;
import com.kienluu.jobfinderbackend.websocket.mapper.MessageMapper;
import com.kienluu.jobfinderbackend.websocket.model.ConversationCreateRequest;
import com.kienluu.jobfinderbackend.websocket.repository.ChatMessageRepository;
import com.kienluu.jobfinderbackend.websocket.repository.ConversationRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ConversationService {
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MessageMapper mapper;


    public ConversationDto createConversation(ConversationCreateRequest request) {
        String senderId = request.getSenderId();
        String recipientId = request.getRecipientId();
        Optional<Conversation> existingConv = conversationRepository.findByTwoUsers(senderId, recipientId);
        if (existingConv.isPresent()) {
            throw new RuntimeException("Conversation between these users already exists");
        }

        UserEntity recipient = userRepository.findById(recipientId).orElseThrow(() -> new RuntimeException("User not found"));
        CompanyEntity sender = companyRepository.findCompanyById(senderId).orElseThrow(() -> new RuntimeException("Company not found"));

        Date date = new Date();
        Conversation conversation = Conversation.builder()
                .type(request.getType())
                .lastMessage(request.getMessage())
                .receiver(recipient)
                .sender(sender)
                .modifiedAt(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .id(request.getId())
                .build();
        conversation = conversationRepository.save(conversation);

        return mapper.toDto(conversation);

    }

    public List<ConversationDto> findAllConversationsByUserId(String userId) {
        List<Conversation> conversations = conversationRepository.findConversationByUserId(userId);
        return conversations.stream().map(mapper::toDto).toList();
    }

    public ConversationDto findConversationByExactTwoUsers(String senderId, String recipientId) {
        Conversation conversation = conversationRepository.findByTwoUsers(senderId, recipientId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        return mapper.toDto(conversation);
    }

    @Transactional
    public ChatMessageDto sendMessage(ChatMessageDto message) {
        var conversation = conversationRepository.findConversationById(message.getConversationId());
        var content = message.getContent();
        if (content.length() > 255) {
            content = content.substring(0, 255);
        }
        conversation.setLastMessage(content);
        conversation.setModifiedAt(LocalDateTime.now());
        conversation.setType(message.getType());

        conversationRepository.save(conversation);
        ChatMessage entity = mapper.toEntity(message);
        entity= chatMessageRepository.save(entity);
        return mapper.toDto(entity);

    }


}
