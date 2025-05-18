package com.kienluu.jobfinderbackend.service;

public interface FirebaseNotificationService {
    void sendNotification(String userId, String title, String body, String screen);
}
