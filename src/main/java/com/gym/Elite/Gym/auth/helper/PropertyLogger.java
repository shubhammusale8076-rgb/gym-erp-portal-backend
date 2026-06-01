package com.gym.Elite.Gym.auth.helper;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PropertyLogger {

    @Value("${integration.service.url}")
    private String integrationUrl;

    @Value("${INTEGRATION_SERVICE_URL:NOT_FOUND}")
    private String envValue;

    @PostConstruct
    public void init() {
        log.info("integration.service.url = [{}]", integrationUrl);
        log.info("INTEGRATION_SERVICE_URL = [{}]", envValue);
    }
}