package com.kienluu.jobfinderbackend.websocket.controller;

import com.kienluu.jobfinderbackend.entity.notification.BanNotification;
import com.kienluu.jobfinderbackend.websocket.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping("/{id}/status/update/{status}")
    public ResponseEntity<Object> updateNotificationStatus(@PathVariable Long id, @PathVariable String status) {
        try{
            notificationService.updateNotificationStatus(id,status);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{userId}/all")
    public ResponseEntity<Object> getAllNotifications(@PathVariable String userId) {
        try{
            List<BanNotification> notifications = notificationService.getAllNotificationsByUserId(userId);
            return ResponseEntity.ok().body(notifications);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{userId}/delivery")
    public ResponseEntity<Object> countAllNotificationsDelivery(@PathVariable String userId) {
        try{
            Long count = notificationService.getNotificationsByUserIdDelivered(userId);
            return ResponseEntity.ok().body(count);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
