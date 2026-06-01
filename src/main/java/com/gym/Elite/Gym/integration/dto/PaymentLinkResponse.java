package com.gym.Elite.Gym.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentLinkResponse {
    private UUID integrationTransactionId;
    private String universalPaymentLink;
    private String razorpayOrderId;
    private String transactionId;
    private String correlationId;
    private WhatsAppDeliveryStatus whatsappStatus;
    private String whatsappError;
}
