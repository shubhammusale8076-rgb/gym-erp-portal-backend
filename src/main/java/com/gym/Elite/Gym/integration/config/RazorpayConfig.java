package com.gym.Elite.Gym.integration.config;


import lombok.Data;

@Data
public class RazorpayConfig {

    private String keyId;
    private String keySecret;
    private String webhookSecret;
}
