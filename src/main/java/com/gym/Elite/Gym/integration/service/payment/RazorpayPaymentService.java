package com.gym.Elite.Gym.integration.service.payment;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RazorpayPaymentService {

    public Map<String, Object> createOrder(String tenantId, Integer amount) {

        Map<String, Object> orderRequest = new HashMap<>();
        orderRequest.put("amount", amount * 100); // paise
        orderRequest.put("currency", "INR");

        Map<String, String> notes = new HashMap<>();
        notes.put("tenantId", tenantId);

        orderRequest.put("notes", notes);

        // call Razorpay API (using their SDK or RestTemplate)

        return orderRequest;
    }
}