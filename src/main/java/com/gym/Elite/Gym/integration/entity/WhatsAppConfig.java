package com.gym.Elite.Gym.integration.entity;

import lombok.Data;

@Data
public class WhatsAppConfig {

    private String accessToken;
    private String phoneNumberId;
    private String webhookVerifyToken;
}
