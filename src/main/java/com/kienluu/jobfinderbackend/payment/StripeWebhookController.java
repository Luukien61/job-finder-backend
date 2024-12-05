package com.kienluu.jobfinderbackend.payment;

import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
@Slf4j
@RestController
@RequestMapping("/webhook")
public class StripeWebhookController {

    @PostMapping("/stripe")
    public ResponseEntity<?> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        String endpointSecret = "whsec_your_webhook_secret";

        try {
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

            if ("checkout.session.completed".equals(event.getType())) {
                Session session = (Session) event.getDataObjectDeserializer().getObject().get();
                String email = session.getCustomerDetails().getEmail();
                String subscriptionId = session.getSubscription();



                // Lưu trạng thái thanh toán trong database
                log.info("Email: " + email);
                log.info("Subscription: " + subscriptionId);
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/verify-session")
    public ResponseEntity<?> verifySession(@RequestBody Map<String, String> request) {
        String sessionId = request.get("sessionId");

        try {
            // Lấy thông tin session từ Stripe
            Session session = Session.retrieve(sessionId);

            if ("paid".equals(session.getPaymentStatus())) {
                // Cập nhật trạng thái người dùng trong database
                String email = session.getCustomerDetails().getEmail();
                String subscriptionId = session.getSubscription();
                Subscription subscription = Subscription.retrieve(subscriptionId);
                String planId = subscription.getItems().getData().getFirst().getPlan().getId();
                String planName = subscription.getItems().getData().getFirst().getPlan().getProduct();

                Long periodEnd = subscription.getCurrentPeriodEnd();
                String formattedDate = Instant.ofEpochSecond(periodEnd)
                        .atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy"));

                String status = subscription.getStatus();
                // Thực hiện logic cập nhật...
                log.info("Email: " + email);
                log.info("Subscription: " + subscriptionId);
                log.info("Plan: " + planId);
                log.info("Plan: " + planName);
                log.info("Status: " + status);
                log.info("Current Period End: " + formattedDate);

                if ("active".equals(subscription.getStatus())) {
                    // Người dùng vẫn đang sử dụng dịch vụ
                } else if ("past_due".equals(subscription.getStatus())) {
                    // Gửi email nhắc nhở thanh toán
                } else if ("canceled".equals(subscription.getStatus())) {
                    // Hủy quyền truy cập dịch vụ
                }

                return ResponseEntity.ok(Map.of("success", true));
            } else {
                return ResponseEntity.ok(Map.of("success", false));
            }
        } catch (StripeException e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
