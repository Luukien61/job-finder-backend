package com.kienluu.jobfinderbackend.payment;

import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Price;
import com.stripe.model.PriceCollection;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                Long planPrice =  subscription.getItems().getData().getFirst().getPlan().getAmount();
                Long periodEnd = subscription.getCurrentPeriodEnd();
                String id = subscription.getId();
                String formattedDate = Instant.ofEpochSecond(periodEnd)
                        .atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy"));

                String status = subscription.getStatus();
                // Thực hiện logic cập nhật...
                log.info("Email: " + email);
                log.info("Subscription: " + subscriptionId);
                log.info("Plan: " + planId);
                log.info("Plan price: " + planPrice);
                log.info("Status: " + status);
                log.info("Current Period End: " + formattedDate);
                log.info("ID: " + id);

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


    @PostMapping("/create-checkout-session")
    public ResponseEntity<Map<String, String>> createCheckoutSession(@RequestBody Map<String, String> request) {

        try {
            String priceId = request.get("priceId");

            // Tạo một Checkout Session
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                    .setSuccessUrl("https://your-frontend.com/success")
                    .setCancelUrl("https://your-frontend.com/cancel")
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setPrice(priceId)
                                    .setQuantity(1L)
                                    .build()
                    )
                    .build();

            Session session = Session.create(params);

            // Trả về URL để redirect đến Stripe Checkout
            Map<String, String> response = new HashMap<>();
            response.put("url", session.getUrl());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/prices")
    public ResponseEntity<List<Map<String, Object>>> getPrices() {

        try {
            // Lấy danh sách prices
            Map<String, Object> params = new HashMap<>();
            PriceCollection prices = Price.list(params);

            // Chuyển đổi dữ liệu để gửi về frontend
            List<Map<String, Object>> priceDetails = prices.getData().stream().map(price -> {
                Map<String, Object> details = new HashMap<>();
                details.put("id", price.getId());
                details.put("product", price.getProduct());
                details.put("unit_amount", price.getUnitAmount());
                details.put("currency", price.getCurrency());
                details.put("interval", price.getRecurring() != null ? price.getRecurring().getInterval() : "one_time");
                return details;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(priceDetails);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}

/*
sub_1QSqeuFMfEC9tDRA74MHDvM0

09:40:37.823 [Test worker] INFO com.kienluu.jobfinderbackend.payment.StripeServiceTest -- [{product=prod_RLQljdkBQsYBH0, currency=vnd, interval=day, id=price_1QSjuNFMfEC9tDRAN1c27wB9, unit_amount=200000}, {product=prod_RLNKJ1UDZ4FUdB, currency=vnd, interval=month, id=price_1QSgZvFMfEC9tDRACzaBLGJW, unit_amount=5000000}, {product=prod_RLNFfMLbTol7FK, currency=vnd, interval=year, id=price_1QSgUeFMfEC9tDRAwdNEkANG, unit_amount=5000000}, {product=prod_RLNEo0klDpuWTM, currency=vnd, interval=month, id=price_1QSgTaFMfEC9tDRADZGlzGlM, unit_amount=500000}, {product=prod_RLNDrPXOgHXmvG, currency=vnd, interval=month, id=price_1QSgSiFMfEC9tDRA4JMu1Snv, unit_amount=0}]
 */