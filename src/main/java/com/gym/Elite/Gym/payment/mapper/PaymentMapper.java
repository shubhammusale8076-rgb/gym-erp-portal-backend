package com.gym.Elite.Gym.payment.mapper;

import com.gym.Elite.Gym.payment.dto.PaymentItemDTO;
import com.gym.Elite.Gym.payment.dto.PaymentResponseDTO;
import com.gym.Elite.Gym.payment.entity.Payment;
import com.gym.Elite.Gym.payment.entity.PaymentItem;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class PaymentMapper {

    public PaymentResponseDTO mapToPaymentDTO(Payment payment) {
        return PaymentResponseDTO.builder()
                .id(payment.getId())
                .transactionReference(payment.getTransactionReference())
                .totalAmount(payment.getTotalAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus() != null ? payment.getStatus().name() : null)
                .paymentDate(payment.getPaymentDate())
                .items(payment.getItems().stream().map(this::mapToItemDTO).collect(Collectors.toList()))
                .build();
    }

    private PaymentItemDTO mapToItemDTO(PaymentItem item) {
        return PaymentItemDTO.builder()
                .name(item.getName())
                .amount(item.getAmount())
                .build();
    }
}
