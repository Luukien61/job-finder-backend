package com.kienluu.jobfinderbackend.payment;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.stereotype.Service;

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


}
