package com.gym.Elite.Gym.webManagement.dto.contactUsDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OperatingHoursDto {

    private String dayOfWeek; // or Enum later
    private String openTime;  // "05:00"
    private String closeTime; // "23:00"
    private Boolean isClosed;
}