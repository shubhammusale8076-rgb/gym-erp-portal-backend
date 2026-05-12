package com.gym.Elite.Gym.crm.integration.client;

import com.gym.Elite.Gym.crm.integration.dto.CalendarRequest;
import com.gym.Elite.Gym.crm.integration.dto.EventResponse;
import com.gym.Elite.Gym.crm.integration.fallback.GoogleCalendarFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "google-calendar-integration", 
    url = "${integration.service.url}", 
    fallback = GoogleCalendarFallback.class
)
public interface GoogleCalendarIntegrationClient {

    @PostMapping("/api/integration/google/calendar/events")
    EventResponse createEvent(@RequestBody CalendarRequest request);
}
