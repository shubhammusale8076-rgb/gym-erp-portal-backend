package com.gym.Elite.Gym.crm.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {
    private String externalId;
    private String status;
    private String meetingLink;
    private String errorMessage;
}

