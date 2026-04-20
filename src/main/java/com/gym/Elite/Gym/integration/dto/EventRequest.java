package com.gym.Elite.Gym.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequest {
    private String eventId;
    private String eventType;
    private String tenantId;

    @JsonProperty("data")
    private Object payload;
}
