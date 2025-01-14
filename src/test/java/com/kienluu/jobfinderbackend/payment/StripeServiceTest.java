package com.kienluu.jobfinderbackend.payment;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.PriceCollection;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StripeServiceTest {


    private static final Logger log = LoggerFactory.getLogger(StripeServiceTest.class);

    @Test
    void getPrice() throws StripeException {
        Stripe.apiKey = "sk_test_51QSgLlFMfEC9tDRAxnWcGeOwUHWfpeBMbQlUXbbTxqUvTFKve4GcPj6ieXAynGjfDrnp8eLweci6cf5h9eTFfrwy00JYWwL0yP";
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
        }).toList();

        log.info(priceDetails.toString());
        assertNotNull(prices);
    }
}