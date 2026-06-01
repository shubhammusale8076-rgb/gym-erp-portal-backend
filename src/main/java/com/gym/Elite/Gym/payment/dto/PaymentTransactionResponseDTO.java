package com.gym.Elite.Gym.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentTransactionResponseDTO {
    private UUID id;
    private UUID memberId;
    private UUID membershipId;
    private Double amount;
    private String paymentStatus;
    private String razorpayPaymentLinkId;
    private String paymentLink;
    private String syncStatus;
    private Integer retryCount;
    private LocalDateTime createdOn;
}
