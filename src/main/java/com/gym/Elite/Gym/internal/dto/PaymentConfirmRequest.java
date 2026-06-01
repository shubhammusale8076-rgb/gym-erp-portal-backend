package com.gym.Elite.Gym.internal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentConfirmRequest {
    private String razorpayPaymentLinkId;
    private String paymentId;
    private Double amount;
    private UUID tenantId;
    private String status;
}
