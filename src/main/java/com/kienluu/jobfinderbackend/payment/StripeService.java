package com.kienluu.jobfinderbackend.payment;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.PriceCollection;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StripeService {

    public String createCheckoutSession(String email, String planId) throws StripeException {
        Stripe.apiKey = "sk_test_your_secret_key"; // Thay bằng Stripe Secret Key của bạn

        // Tạo Checkout Session
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION) // Đăng ký định kỳ
                .setCustomerEmail(email) // Email của khách hàng
                .setSuccessUrl("https://your-frontend.com/success?session_id={CHECKOUT_SESSION_ID}") // Redirect khi thành công
                .setCancelUrl("https://your-frontend.com/cancel") // Redirect khi hủy
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPrice(planId) // ID gói thanh toán trong Stripe
                                .setQuantity(1L)
                                .build())
                .build();

        Session session = Session.create(params);
        return session.getUrl(); // Trả về URL thanh toán
    }

    public boolean isSubscriptionActive(String subscriptionId) throws StripeException {
        Stripe.apiKey = "sk_test_your_secret_key";

        Subscription subscription = Subscription.retrieve(subscriptionId);
        return "active".equals(subscription.getStatus());
    }

    public Subscription getSubscriptionDetails(String subscriptionId) throws StripeException {
        return Subscription.retrieve(subscriptionId);
    }

    public void checkState(){
        try {
            String subscriptionId = "sub_1QSqeuFMfEC9tDRA74MHDvM0";
            Subscription subscription = Subscription.retrieve(subscriptionId);

            // Kiểm tra trạng thái subscription
            if ("active".equals(subscription.getStatus())) {
                System.out.println("Subscription is active.");
                // Xử lý logic cho trạng thái active
            } else if ("canceled".equals(subscription.getStatus())) {
                System.out.println("Subscription is canceled.");
                // Xử lý logic cho trạng thái canceled
            } else {
                System.out.println("Subscription status: " + subscription.getStatus());
                // Xử lý logic khác nếu cần
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error fetching subscription details.");
        }
    }

    public void getPrice(){
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
            }).toList();


        } catch (Exception e) {
            log.error("Error fetching price.");
        }
    }


}
