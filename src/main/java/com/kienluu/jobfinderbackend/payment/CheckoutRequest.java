package com.kienluu.jobfinderbackend.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CheckoutRequest {
    private String email;
    private String plan;
}
