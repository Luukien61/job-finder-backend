package com.kienluu.jobfinderbackend.payment;

import com.kienluu.jobfinderbackend.entity.CompanyPlan;
import com.kienluu.jobfinderbackend.service.implement.SubscriptionService;
import com.stripe.model.Event;
import com.stripe.model.Price;
import com.stripe.model.PriceCollection;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class StripeWebhookController {
    private final SubscriptionService subscriptionService;
    @Value("${stripe.checkout.register.successUrl}")
    private String registerSuccessUrl;
    @Value("${stripe.checkout.register.cancelUrl}")
    private String registerCancelUrl;

    @PostMapping("/stripe")
    public ResponseEntity<?> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        String endpointSecret = "whsec_webhook_secret";

        try {
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

            if ("checkout.session.completed".equals(event.getType())) {
                Session session = (Session) event.getDataObjectDeserializer().getObject().get();
                String email = session.getCustomerDetails().getEmail();
                String subscriptionId = session.getSubscription();

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
        String companyId = request.get("companyId");
        try {
            subscriptionService.saveNewSubscription(sessionId, companyId);
            CompanyPlan plan = subscriptionService.getCompanyPlan(companyId);
            return ResponseEntity.ok(plan);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }


    @PostMapping("/create-checkout-session")
    public ResponseEntity<Map<String, String>> createCheckoutSession(@RequestBody Map<String, String> request) {

        try {
            String priceId = request.get("priceId");
            Session session = subscriptionService
                    .createCheckoutSession(priceId,registerSuccessUrl, registerCancelUrl );
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
