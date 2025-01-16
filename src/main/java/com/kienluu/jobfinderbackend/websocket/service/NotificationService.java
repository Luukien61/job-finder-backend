package com.kienluu.jobfinderbackend.websocket.service;

import com.kienluu.jobfinderbackend.entity.notification.AcceptNotification;
import com.kienluu.jobfinderbackend.entity.notification.BanNotification;
import com.kienluu.jobfinderbackend.model.NotificationStatus;
import com.kienluu.jobfinderbackend.repository.AcceptNotificationRepository;
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
    private final AcceptNotificationRepository acceptNotificationRepository;

    @EventListener(classes = BanNotification.class)
    public void onBan(BanNotification banEvent) {
        simpMessagingTemplate.convertAndSendToUser(banEvent.getUserId(),"/notifications", banEvent);
    }


    public List<?> getAllNotificationsByUserId(String userId) {
        if (userId.startsWith("company_")) {
            return banNotificationRepository.findAllByUserId(userId);
        }else {
            return acceptNotificationRepository.findAllByUserId(userId);
        }

    }

    public Long getNotificationsByUserIdDelivered(String userId) {
        if (userId.startsWith("company_")) {
            return banNotificationRepository.countAllNotificationsByUserIdAndStatus(userId, NotificationStatus.DELIVERED);
        }else {
            return acceptNotificationRepository.countAllNotificationsByUserIdAndStatus(userId, NotificationStatus.DELIVERED);
        }
    }

    public void updateNotificationStatus(Long id, String status) {
        BanNotification notification = banNotificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setStatus(NotificationStatus.valueOf(status.toUpperCase()));
        banNotificationRepository.save(notification);
    }


    @EventListener(classes = AcceptNotification.class)
    public void onAccept(AcceptNotification event) {
        simpMessagingTemplate.convertAndSendToUser(event.getUserId(),"/notifications", event);
    }


    public void updateAcceptNotificationStatus(Long id, String status) {
        AcceptNotification notification = acceptNotificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setStatus(NotificationStatus.valueOf(status.toUpperCase()));
        acceptNotificationRepository.save(notification);
    }

}
