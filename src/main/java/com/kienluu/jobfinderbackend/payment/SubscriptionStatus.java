package com.kienluu.jobfinderbackend.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
public enum SubscriptionStatus {
    ACTIVE("active"),
    CANCELLED("cancelled");
    private String status;

    @Override
    public String toString() {
        return this.status;
    }
}
