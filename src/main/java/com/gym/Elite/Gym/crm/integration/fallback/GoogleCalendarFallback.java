package com.gym.Elite.Gym.crm.integration.fallback;

import com.gym.Elite.Gym.crm.integration.client.GoogleCalendarIntegrationClient;
import com.gym.Elite.Gym.crm.integration.dto.CalendarRequest;
import com.gym.Elite.Gym.crm.integration.dto.EventResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GoogleCalendarFallback implements GoogleCalendarIntegrationClient {

    @Override
    public EventResponse createEvent(CalendarRequest request) {
        log.error("Google Calendar Integration Service is unavailable. CorrelationId: {}", request.getCorrelationId());
        return EventResponse.builder()
                .status("FAILED_PROVIDER_UNAVAILABLE")
                .errorMessage("Integration service is currently down. Request queued for retry.")
                .build();
    }
}
