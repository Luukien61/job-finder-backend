package com.kienluu.jobfinderbackend.service.implement;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.kienluu.jobfinderbackend.repository.UserRepository;
import com.kienluu.jobfinderbackend.service.FirebaseNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class FirebaseNotificationServiceImp implements FirebaseNotificationService {

    private final UserRepository userRepository;

    @Override
    public void sendNotification(String userId, String title, String body, String screen) {
        val user = userRepository.findUserById(userId).orElseThrow(() ->
                new IllegalArgumentException("User not found"));
        val fcmToken = user.getFcmToken();
        if (fcmToken == null || fcmToken.isBlank()) {
            throw new IllegalStateException("FCM token not available for user $userId");
        }
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setToken(fcmToken)
                .putData("screen", screen)
                .setNotification(notification)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
    }
}
