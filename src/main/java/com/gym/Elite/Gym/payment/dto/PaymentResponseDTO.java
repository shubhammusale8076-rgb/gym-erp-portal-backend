package com.gym.Elite.Gym.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDTO {

    private UUID id;
    private String transactionReference;
    private Double totalAmount;
    private String currency;
    private String status;
    private String paymentMethod;
    private String cardLast4;
    private LocalDateTime paymentDate;
    private List<PaymentItemDTO> items;
}