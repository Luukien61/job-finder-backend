package com.kienluu.jobfinderbackend.websocket.controller;

import com.kienluu.jobfinderbackend.websocket.dto.ConversationDto;
import com.kienluu.jobfinderbackend.websocket.entity.ChatMessage;
import com.kienluu.jobfinderbackend.websocket.entity.Conversation;
import com.kienluu.jobfinderbackend.websocket.entity.RTCSignal;
import com.kienluu.jobfinderbackend.websocket.model.ConversationCreateRequest;
import com.kienluu.jobfinderbackend.websocket.model.Participant;
import com.kienluu.jobfinderbackend.websocket.repository.ChatMessageRepository;
import com.kienluu.jobfinderbackend.websocket.repository.ConversationRepository;
import com.kienluu.jobfinderbackend.websocket.service.ConversationService;
import com.kienluu.jobfinderbackend.websocket.service.ParticipantService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class ChatController {
    private SimpMessagingTemplate simpMessagingTemplate;
    private ConversationRepository conversationRepository;
    private ChatMessageRepository chatMessageRepository;
    private ConversationService conversationService;
    private ParticipantService participantService;

    @MessageMapping("/sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }

    @MessageMapping("/private-message")
    public ChatMessage recMessage(@Payload ChatMessage message) {
        simpMessagingTemplate.convertAndSendToUser(message.getRecipientId(), "/private", message);
        var conversation = conversationRepository.findConversationById(message.getConversationId());
        var content = message.getContent();
        if (content.length() > 255) {
            content = content.substring(0, 255);
        }
        conversation.setLastMessage(content);
        conversation.setModifiedAt(LocalDateTime.now());
        conversation.setType(message.getType());
        conversationRepository.save(conversation);
        chatMessageRepository.save(message);
        return message;
    }

    @GetMapping("/chat/all/{userId}")
    public ResponseEntity<List<ConversationDto>> getAllConversationByUserId(@PathVariable String userId) {
        try{
            List<ConversationDto> response = conversationService.findAllConversationsByUserId(userId);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/participant/{id}")
    public ResponseEntity<Object> getParticipant(@PathVariable String id) {
        try {
            Participant participant = participantService.findParticipantById(id);
            return ResponseEntity.ok(participant);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("The user id does not exist");
        }
    }

    @GetMapping("/message/{conversationId}")
    public ResponseEntity<List<ChatMessage>> getMessage(@PathVariable Long conversationId) {
        var messages = chatMessageRepository.findChatMessageByConversationIdOrderByTimestampDesc(conversationId);
        return ResponseEntity.ok(messages);
    }


    @GetMapping("/conversation")
    public ResponseEntity<Object> getConversations(@RequestParam("user1") String user1, @RequestParam("user2") String user2) {
        try {
            ConversationDto conversation = conversationService.findConversationByExactTwoUsers(user1, user2);
            return ResponseEntity.ok(conversation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("The conversation id does not exist");
        }
    }

    @PostMapping("/conversation/private")
    public ResponseEntity<Object> createConversation(@RequestBody ConversationCreateRequest request) {
        try {
            ConversationDto conversation = conversationService.createConversation(request);
            return ResponseEntity.ok(conversation);
        } catch (Exception e) {
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }


    @MessageMapping("/webrtc-signal")
    public void handleWebRTCSignal(RTCSignal signal) {
        log.info("Receiving a video call to... {}, {}",signal.getTargetUserId(), signal.getType());
        simpMessagingTemplate.convertAndSendToUser(
                signal.getTargetUserId(),
                "/webrtc",
                signal
        );
    }
}
