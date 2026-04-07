package com.gym.Elite.Gym.integration.utill;

import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Component
public class RazorpaySignatureUtil {

    public boolean verify(String payload, String actualSignature, String secret) {
        try {
            String expectedSignature = hmacSha256(payload, secret);
            return expectedSignature.equals(actualSignature);
        } catch (Exception e) {
            return false;
        }
    }

    private String hmacSha256(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey =
                new SecretKeySpec(key.getBytes(), "HmacSHA256");
        mac.init(secretKey);

        byte[] raw = mac.doFinal(data.getBytes());
        return bytesToHex(raw);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder(2 * bytes.length);
        for (byte b : bytes)
            hex.append(String.format("%02x", b));
        return hex.toString();
    }
}
