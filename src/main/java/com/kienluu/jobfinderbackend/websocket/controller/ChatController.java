package com.kienluu.jobfinderbackend.websocket.controller;


import com.kienluu.jobfinderbackend.websocket.entity.ChatMessage;
import com.kienluu.jobfinderbackend.websocket.entity.Conversation;
import com.kienluu.jobfinderbackend.websocket.entity.Participant;
import com.kienluu.jobfinderbackend.websocket.repository.ChatMessageRepository;
import com.kienluu.jobfinderbackend.websocket.repository.ConversationRepository;
import com.kienluu.jobfinderbackend.websocket.service.ParticipantService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
@Slf4j
@RestController
@AllArgsConstructor
public class ChatController{
    private SimpMessagingTemplate simpMessagingTemplate;
    private ConversationRepository conversationRepository;
    private ChatMessageRepository chatMessageRepository;
    private ParticipantService participantService;

    @MessageMapping("/private-message")
    public ChatMessage recMessage(@Payload ChatMessage message){
        simpMessagingTemplate.convertAndSendToUser(message.getRecipientId(),"/private",message);
        var conversation = conversationRepository.findConversationById(message.getConversationId());
        conversation.setLastMessage(message.getContent());
        conversation.setModifiedAt(LocalDateTime.now());
        conversationRepository.save(conversation);
        chatMessageRepository.save(message);
        return message;
    }

    @GetMapping("/chat/all/{id}")
    public ResponseEntity<List<Conversation>> getAllConversation(@PathVariable String id){
        List<Conversation> conversations = conversationRepository.findConversationByUser1IdOrUser2Id(id,id);
        return ResponseEntity.ok(conversations);
    }
    @GetMapping("/participant/{id}")
    public ResponseEntity<Participant> getParticipant(@PathVariable String id){
        Participant participant = participantService.findParticipantById(id);
        return ResponseEntity.ok(participant);
    }

    @GetMapping("/message/{conversationId}")
    public ResponseEntity<List<ChatMessage>> getMessage(@PathVariable String conversationId){
        var messages=chatMessageRepository.findChatMessageByConversationIdOrderByTimestampDesc(conversationId);
        return ResponseEntity.ok(messages);
    }
}