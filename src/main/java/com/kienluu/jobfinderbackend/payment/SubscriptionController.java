package com.kienluu.jobfinderbackend.payment;

import com.kienluu.jobfinderbackend.entity.CompanyPlan;
import com.kienluu.jobfinderbackend.service.implement.SubscriptionService;
import com.stripe.Stripe;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.param.SubscriptionUpdateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    @Value("${stripe.checkout.upgrade.successUrl}")
    private String upgradeSuccessUrl;
    @Value("${stripe.checkout.upgrade.cancelUrl}")
    public String upgradeCacelUrl;

    @GetMapping("/{companyId}")
    public ResponseEntity<Object> getSubscription(@PathVariable String companyId) {
        try{
            CompanyPlan plan = subscriptionService.getCompanyPlan(companyId);
            return new ResponseEntity<>(plan, HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/cancel-subscription")
    public ResponseEntity<String> cancelSubscription(@RequestBody Map<String, String> request) {

        try {
            String subscriptionId = request.get("companyId");
            String priceId = request.get("priceId");
            subscriptionService.cancelPlan(subscriptionId, priceId);
            return ResponseEntity.ok("Subscription canceled successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/upgrade-session")
    public ResponseEntity<Object> createUpgradeSession(@RequestBody Map<String, String> request) {
        try {
            String priceId = request.get("priceId");
            Session session = subscriptionService
                    .createCheckoutSession(priceId,upgradeSuccessUrl, upgradeCacelUrl );
            Map<String, String> response = new HashMap<>();
            response.put("url", session.getUrl());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/update-subscription")
    public ResponseEntity<String> updateSubscription(@RequestBody Map<String, String> request) {
        String sessionId = request.get("sessionId");
        String companyId = request.get("companyId");
        try {

            String subscriptionId = request.get("subscriptionId");
            String newPriceId = request.get("newPriceId");

            Subscription subscription = Subscription.retrieve(subscriptionId);

            SubscriptionUpdateParams params = SubscriptionUpdateParams.builder()
                    .addItem(
                            SubscriptionUpdateParams.Item.builder()
                                    .setPrice(newPriceId)
                                    .build()
                    )
                    .build();

            subscription.update(params);

            return ResponseEntity.ok("Subscription updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }


    @GetMapping("/plan/priority")
    public ResponseEntity<Object> getPlanPriority() {
        try{
            Map<String, Integer> priority = subscriptionService.getPlanPriority();
            return new ResponseEntity<>(priority, HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
