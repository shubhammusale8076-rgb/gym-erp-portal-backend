package com.gym.Elite.Gym.payment.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TransactionDTO {

    private String title;
    private LocalDateTime date;

    private Double amount;
    private String status; // SUCCESS / FAILED
    private String type;   // SUBSCRIPTION / FOOD / etc
}
