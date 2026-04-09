package com.gym.Elite.Gym.integration.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class ValidateService {

    public void validate(String provider, Map<String, Object> config) {

        switch (provider.toUpperCase()) {

            case "RAZORPAY":
                validateRazorpay(config);
                break;

            case "WHATSAPP":
                validateWhatsApp(config);
                break;

            default:
                throw new RuntimeException("Unsupported provider");
        }
    }

    private void validateWhatsApp(Map<String, Object> config) {

        String token = (String) config.get("accessToken");
        String phoneId = (String) config.get("phoneNumberId");

        if (token == null || phoneId == null) {
            throw new RuntimeException("Missing WhatsApp config");
        }

        // Optional: test message or API call
    }

    private void validateRazorpay(Map<String, Object> config) {

        String keyId = (String) config.get("keyId");
        String keySecret = (String) config.get("keySecret");

        if (keyId == null || keySecret == null) {
            throw new RuntimeException("Missing Razorpay keys");
        }

        // 🔥 Test API call
        try {
            String url = "https://api.razorpay.com/v1/orders";

            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(keyId, keySecret);

            HttpEntity<?> request = new HttpEntity<>(headers);

            new RestTemplate().exchange(url, HttpMethod.GET, request, String.class);

        } catch (Exception e) {
            throw new RuntimeException("Invalid Razorpay credentials");
        }
    }
}
