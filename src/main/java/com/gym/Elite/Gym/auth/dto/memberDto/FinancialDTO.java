package com.gym.Elite.Gym.auth.dto.memberDto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class FinancialDTO {

    private LocalDate nextPaymentDate;
    private Double amountDue;
}
