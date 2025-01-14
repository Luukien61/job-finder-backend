package com.kienluu.jobfinderbackend.websocket.service;

import com.kienluu.jobfinderbackend.entity.notification.BanNotification;
import com.kienluu.jobfinderbackend.model.NotificationStatus;
import com.kienluu.jobfinderbackend.repository.BanNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final BanNotificationRepository banNotificationRepository;

    @EventListener(classes = BanNotification.class)
    public void onBan(BanNotification banEvent) {
        simpMessagingTemplate.convertAndSendToUser(banEvent.getUserId(),"/notifications", banEvent);
    }

    public List<BanNotification> getAllNotificationsByUserId(String userId) {
        return banNotificationRepository.findAllByUserId(userId);
    }

    public Long getNotificationsByUserIdDelivered(String userId) {
        return banNotificationRepository.countAllNotificationsByUserIdAndStatus(userId, NotificationStatus.DELIVERED);
    }

    public void updateNotificationStatus(Long id, String status) {
        BanNotification notification = banNotificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setStatus(NotificationStatus.valueOf(status.toUpperCase()));
        banNotificationRepository.save(notification);
    }




}
