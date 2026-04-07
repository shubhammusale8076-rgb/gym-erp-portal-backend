package com.gym.Elite.Gym.payment.mapper;

import com.gym.Elite.Gym.payment.dto.PaymentItemDTO;
import com.gym.Elite.Gym.payment.dto.PaymentResponseDTO;
import com.gym.Elite.Gym.payment.entity.Payment;
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
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethod())
                .cardLast4(payment.getCardLast4())
                .paymentDate(payment.getPaymentDate())
                .items(payment.getItems().stream()
                        .map(i -> {
                            PaymentItemDTO dto = new PaymentItemDTO();
                            dto.setName(i.getName());
                            dto.setDescription(i.getDescription());
                            dto.setAmount(i.getAmount());
                            return dto;
                        }).collect(Collectors.toList()))
                .build();
    }
}
