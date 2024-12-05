package com.kienluu.jobfinderbackend.payment;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;



@Component
public class StripeConfig {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @PostConstruct
    public void setup() {
        // Đặt API key từ file cấu hình
        Stripe.apiKey = stripeApiKey;
    }
}
