package com.gym.Elite.Gym.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequestDTO {

    private UUID memberId;
    private UUID tenantId;

    private Double subtotal;
    private Double taxAmount;
    private Double discountAmount;
    private Double totalAmount;

    private String currency;
    private String paymentMethod;

    private String cardLast4;

    private UUID planId;
    private UUID subscriptionId; // optional (for renewal)

    private List<PaymentItemDTO> items;
}