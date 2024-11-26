package com.kienluu.jobfinderbackend.websocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:5173")
                .setAllowedOriginPatterns("**");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app"); //(1)
        registry.enableSimpleBroker( "/user"); //(2)
        registry.setUserDestinationPrefix("/user");//(3)
    }
}
/*
1. prefix that users send messages to.
example, users have to send messages to /app/private-message
2. Các tin nhắn gửi đến đích /user/... sẽ được broker xử lý và chuyển tiếp
đến các client đã đăng ký lắng nghe đích đó.
3. ác định tiền tố để ánh xạ các destination dành riêng cho từng người dùng (user-specific destinations).
Khi server gửi tin nhắn tới một người dùng cụ thể bằng cách sử dụng convertAndSendToUser,
Spring sẽ tự động thêm tiền tố /user vào destination.
 */