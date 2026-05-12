package com.gym.Elite.Gym.payment.dto;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodDto {

    private String type;
    private String last4;
    private String expiry;
}