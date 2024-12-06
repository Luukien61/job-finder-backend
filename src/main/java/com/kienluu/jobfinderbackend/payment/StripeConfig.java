package com.kienluu.jobfinderbackend.payment;

import com.kienluu.jobfinderbackend.entity.CompanyPlan;
import com.kienluu.jobfinderbackend.repository.CompanyPlanRepository;
import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class StripeConfig {

    private final CompanyPlanRepository companyPlanRepository;

    @Value("${stripe.checkout.plan.ultimate}")
    private String ULTIMATE_PLAN;
    @Value("${stripe.checkout.plan.pro}")
    private String PRO_PLAN;
    @Value("${stripe.checkout.plan.basic.name}")
    private String BASIC_PLAN;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @PostConstruct
    public void setup() {
        Stripe.apiKey = stripeApiKey;
    }

    @PostConstruct
    public void initPlan() {
        List<CompanyPlan> all = companyPlanRepository.findAll();
        if (all.isEmpty()) {
            CompanyPlan ultimatePlan = new CompanyPlan();
            ultimatePlan.setName(ULTIMATE_PLAN);
            ultimatePlan.setDescription(ULTIMATE_PLAN);
            ultimatePlan.setPrice(5000000L);
            ultimatePlan.setPriority(20);
            ultimatePlan.setPeriod("year");
            ultimatePlan.setId("price_1QSgUeFMfEC9tDRAwdNEkANG");
            ultimatePlan.setPriceId("price_1QSgUeFMfEC9tDRAwdNEkANG");
            ultimatePlan.setProductId("prod_RLNFfMLbTol7FK");

            CompanyPlan proPlan = CompanyPlan.builder()
                    .id("price_1QSgTaFMfEC9tDRADZGlzGlM")
                    .price(500000L)
                    .period("month")
                    .priority(15)
                    .productId("prod_RLNEo0klDpuWTM")
                    .priceId("price_1QSgTaFMfEC9tDRADZGlzGlM")
                    .name(PRO_PLAN)
                    .build();

            CompanyPlan basicPlan = CompanyPlan.builder()
                    .id("price_1QSw8SFMfEC9tDRA6lC6x28Q")
                    .name(BASIC_PLAN)
                    .price(300000L)
                    .limitPost(30)
                    .priority(10)
                    .productId("prod_RLdPii9sz0QMtX")
                    .period("month")
                    .priceId("price_1QSw8SFMfEC9tDRA6lC6x28Q")
                    .build();
            companyPlanRepository.save(proPlan);
            companyPlanRepository.save(basicPlan);
            companyPlanRepository.save(ultimatePlan);
            companyPlanRepository.flush();
        }

    }
}
