package com.kienluu.jobfinderbackend.payment;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentController {
    private final StripeService stripeService;

    @PostMapping("/api/create-checkout-session")
    public ResponseEntity<?> createCheckoutSession(@RequestBody CheckoutRequest request) {
        try {
            // Tạo Checkout Session
            String sessionUrl = stripeService.createCheckoutSession(request.getEmail(), request.getPlan());
            return ResponseEntity.ok(Map.of("url", sessionUrl));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            // Xác thực webhook
            String endpointSecret = "whsec_your_webhook_secret"; // Lấy từ Stripe Dashboard
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

            // Xử lý sự kiện
            switch (event.getType()) {
                case "checkout.session.completed":
                    handleCheckoutSessionCompleted(event);
                    break;
                default:
                    log.info("Unhandled event type: {}", event.getType());
            }

            return ResponseEntity.ok().build();
        } catch (SignatureVerificationException e) {
            log.error("Webhook signature verification failed: {}", e.getMessage());
            return ResponseEntity.status(400).build();
        }
    }

    private void handleCheckoutSessionCompleted(Event event) {
        Session session = (Session) event.getDataObjectDeserializer().getObject().get();
        String customerId = session.getCustomer(); // ID khách hàng trong Stripe
        String subscriptionId = session.getSubscription(); // ID Subscription

//        // Tìm và cập nhật người dùng trong cơ sở dữ liệu
//        User user = userRepository.findByEmail(session.getCustomerDetails().getEmail());
//        if (user != null) {
//            user.setStripeCustomerId(customerId);
//            user.setStripeSubscriptionId(subscriptionId);
//            user.setAccountType("PRO"); // Hoặc ULTIMATE tùy vào gói
//            userRepository.save(user);
//        }
    }


}
