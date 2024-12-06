package com.kienluu.jobfinderbackend.service.implement;

import com.kienluu.jobfinderbackend.entity.CompanyEntity;
import com.kienluu.jobfinderbackend.entity.CompanyPlan;
import com.kienluu.jobfinderbackend.entity.CompanySubscription;
import com.kienluu.jobfinderbackend.payment.SubscriptionStatus;
import com.kienluu.jobfinderbackend.repository.CompanyPlanRepository;
import com.kienluu.jobfinderbackend.repository.CompanyRepository;
import com.kienluu.jobfinderbackend.repository.CompanySubscriptionRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.param.SubscriptionUpdateParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriptionService {
    private final CompanyRepository companyRepository;
    private final CompanyPlanRepository companyPlanRepository;
    private final CompanySubscriptionRepository companySubscriptionRepository;

    public void saveNewSubscription(String sessionId, String companyId) {
        try {
            Session session = Session.retrieve(sessionId);
            if ("paid".equals(session.getPaymentStatus())) {
                String email = session.getCustomerDetails().getEmail();
                String subscriptionId = session.getSubscription();
                Subscription subscription = Subscription.retrieve(subscriptionId);
                String planId = subscription.getItems().getData().getFirst().getPlan().getId();
                Long planPrice = subscription.getItems().getData().getFirst().getPlan().getAmount();
                Long createAt = subscription.getCurrentPeriodStart();
                Long periodEnd = subscription.getCurrentPeriodEnd();
                String id = subscription.getId();
                String formattedDate = Instant.ofEpochSecond(periodEnd)
                        .atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                String status = subscription.getStatus();
                //LocalDate endTime = LocalDate.parse(formattedDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                LocalDate endTime = Instant.ofEpochSecond(periodEnd)
                        .atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate startTime = Instant.ofEpochSecond(createAt)
                        .atZone(ZoneId.systemDefault()).toLocalDate();
                CompanyEntity company = companyRepository.findCompanyById(companyId)
                        .orElseThrow(() -> new RuntimeException("Company not found"));

                CompanyPlan plan = companyPlanRepository.findById(planId)
                        .orElseThrow(() -> new RuntimeException("Plan not found"));
                CompanySubscription userSubscription = CompanySubscription.builder()
                        .company(company)
                        .plan(plan)
                        .id(subscriptionId)
                        .startDate(startTime)
                        .email(email)
                        .status(status)
                        .endDate(endTime)
                        .build();


                CompanySubscription existCompanySubscription = company.getCompanySubscription();
                if (existCompanySubscription != null) {
                    CompanyPlan companyPlan = company.getCompanySubscription().getPlan();
                    List<CompanySubscription> companySubscriptions = companyPlan.getCompanySubscriptions();
                    companySubscriptions.remove(existCompanySubscription);
                    companyPlan.setCompanySubscriptions(companySubscriptions);
                    companyPlanRepository.save(companyPlan);
                    company.setCompanySubscription(null);
                    companySubscriptionRepository.delete(existCompanySubscription);
                }
                persistSubscription(subscription, company, plan, userSubscription);
            }
        } catch (StripeException e) {
            log.error(e.getMessage());
        }
    }

    private void persistSubscription(Subscription subscription, CompanyEntity company, CompanyPlan plan, CompanySubscription userSubscription) {
        companySubscriptionRepository.save(userSubscription);
        List<CompanySubscription> subscriptions = plan.getCompanySubscriptions();
        subscriptions.add(userSubscription);
        plan.setCompanySubscriptions(subscriptions);
        company.setCompanySubscription(userSubscription);
        companyRepository.save(company);
        companyPlanRepository.save(plan);

        if ("active".equals(subscription.getStatus())) {
            // Người dùng vẫn đang sử dụng dịch vụ
        } else if ("past_due".equals(subscription.getStatus())) {
            // Gửi email nhắc nhở thanh toán
        } else if ("canceled".equals(subscription.getStatus())) {
            // Hủy quyền truy cập dịch vụ
        }
    }

    public CompanyPlan getCompanyPlan(String companyId) {
        CompanyEntity company = companyRepository.findCompanyById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        CompanySubscription subscription = company.getCompanySubscription();
        if (subscription == null) {
            return null;
        }
        if(!Objects.equals(subscription.getStatus(), "active")){
            return null;
        }
        return subscription.getPlan();
    }

    public Map<String, Integer> getPlanPriority() {
        List<CompanyPlan> allPlan = companyPlanRepository.findAll();
        Map<String, Integer> planPriority = new HashMap<>();
        allPlan.forEach(plan -> planPriority.put(plan.getId(), plan.getPriority()));
        return planPriority;
    }

    public Session createCheckoutSession(String priceId, String successUrl, String cancelUrl, String companyId) throws StripeException{
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSuccessUrl("https://your-frontend.com/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("https://your-frontend.com/cancel")
                .setSubscriptionData(
                        SessionCreateParams.SubscriptionData.builder().build()
                )
                .build();
        return Session.create(params);
    }

    public Session createCheckoutSession(String priceId, String successUrl, String cancelUrl) throws StripeException {
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setLocale(SessionCreateParams.Locale.VI)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPrice(priceId)
                                .setQuantity(1L)
                                .build()
                )
                .build();

        return Session.create(params);
    }

    public void upgradePlan(String sessionId, String companyId){
        try {
            Session session = Session.retrieve(sessionId);
            if ("paid".equals(session.getPaymentStatus())) {
                String email = session.getCustomerDetails().getEmail();
                String subscriptionId = session.getSubscription();
                Subscription subscription = Subscription.retrieve(subscriptionId);
                String planId = subscription.getItems().getData().getFirst().getPlan().getId();
                Long planPrice = subscription.getItems().getData().getFirst().getPlan().getAmount();
                Long createAt = subscription.getCurrentPeriodStart();
                Long periodEnd = subscription.getCurrentPeriodEnd();
                String id = subscription.getId();
                String formattedDate = Instant.ofEpochSecond(periodEnd)
                        .atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                String status = subscription.getStatus();
                //LocalDate endTime = LocalDate.parse(formattedDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                LocalDate endTime = Instant.ofEpochSecond(periodEnd)
                        .atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate startTime = Instant.ofEpochSecond(createAt)
                        .atZone(ZoneId.systemDefault()).toLocalDate();
                CompanyEntity company = companyRepository.findCompanyById(companyId)
                        .orElseThrow(() -> new RuntimeException("Company not found"));

                CompanyPlan plan = companyPlanRepository.findById(planId)
                        .orElseThrow(() -> new RuntimeException("Plan not found"));
                CompanySubscription userSubscription = CompanySubscription.builder()
                        .company(company)
                        .plan(plan)
                        .id(subscriptionId)
                        .startDate(startTime)
                        .email(email)
                        .status(status)
                        .endDate(endTime)
                        .build();

                persistSubscription(subscription, company, plan, userSubscription);
            }
        } catch (StripeException e) {
            log.error(e.getMessage());
        }
    }

    public void cancelPlan(String companyId, String planId) throws StripeException {

        CompanyEntity company = companyRepository.findCompanyById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        CompanyPlan plan = companyPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));
        CompanySubscription companySubscription = company.getCompanySubscription();
        String subscriptionId = companySubscription.getId();
        Subscription subscription = Subscription.retrieve(subscriptionId);
//        SubscriptionUpdateParams params = SubscriptionUpdateParams.builder()
//                .setCancelAtPeriodEnd(true)
//                .build();
//        subscription.update(params);
        subscription.cancel();
        //companySubscription.setStatus(SubscriptionStatus.CANCELLED.getStatus());
        company.setCompanySubscription(null);

        List<CompanySubscription> subscriptions = plan.getCompanySubscriptions();
        subscriptions.remove(companySubscription);
        plan.setCompanySubscriptions(subscriptions);
        companyPlanRepository.save(plan);
        companySubscriptionRepository.delete(companySubscription);
        companyRepository.save(company);

    }
}
