package com.kienluu.jobfinderbackend.websocket.controller;

import com.kienluu.jobfinderbackend.service.implement.S3Service;
import com.kienluu.jobfinderbackend.websocket.dto.ChatMessageDto;
import com.kienluu.jobfinderbackend.websocket.dto.ConversationDto;
import com.kienluu.jobfinderbackend.websocket.entity.ChatMessage;
import com.kienluu.jobfinderbackend.websocket.entity.RTCSignal;
import com.kienluu.jobfinderbackend.websocket.model.ConversationCreateRequest;
import com.kienluu.jobfinderbackend.websocket.model.Participant;
import com.kienluu.jobfinderbackend.websocket.repository.ChatMessageRepository;
import com.kienluu.jobfinderbackend.websocket.repository.ConversationRepository;
import com.kienluu.jobfinderbackend.websocket.service.ConversationService;
import com.kienluu.jobfinderbackend.websocket.service.ParticipantService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class ChatController {
    private SimpMessagingTemplate simpMessagingTemplate;
    private ChatMessageRepository chatMessageRepository;
    private ConversationService conversationService;
    private ParticipantService participantService;
    private S3Service s3Service;

    @MessageMapping("/sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }

    @MessageMapping("/private-message")
    public ChatMessageDto recMessage(@Payload ChatMessageDto message) {
        simpMessagingTemplate.convertAndSendToUser(message.getRecipientId(), "/private", message);
        try{
            return conversationService.sendMessage(message);
        }catch (Exception e){
            return null;
        }
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
    public ResponseEntity<Object> getConversations(@RequestParam("sender") String senderId, @RequestParam("receiver") String receiverId) {
        try {
            ConversationDto conversation = conversationService.findConversationByExactTwoUsers(senderId, receiverId);
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
    @PostMapping(value = "/voice", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAudio(@RequestParam("audio") MultipartFile audioFile) {
        try {
            String uploadedFile = s3Service.uploadFile(audioFile);
            return ResponseEntity.ok(uploadedFile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/message")
    public ResponseEntity<Object> updateMessage(@RequestBody ChatMessage chatMessage) {
        try{
            ChatMessage message = chatMessageRepository.findById(chatMessage.getId())
                    .orElseThrow(() -> new RuntimeException("The message id does not exist"));
            message.setCaption(chatMessage.getCaption());
            ChatMessage saved = chatMessageRepository.save(message);
            return ResponseEntity.ok(saved);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
