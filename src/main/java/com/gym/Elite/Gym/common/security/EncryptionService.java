package com.gym.Elite.Gym.common.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Service
public class EncryptionService {

    private final SecretKeySpec secretKey;

    public EncryptionService(@Value("${app.encryption.secret}") String secret) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] key = sha.digest(secret.getBytes(StandardCharsets.UTF_8));
            this.secretKey = new SecretKeySpec(key, "AES");
        } catch (Exception e) {
            throw new RuntimeException("Error initializing encryption key", e);
        }
    }

    public String encrypt(String data) {
        try {
            if (data == null) return null;

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] encrypted = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);

        } catch (Exception ex) {
            throw new RuntimeException("Error encrypting data", ex);
        }
    }

    public String decrypt(String encryptedData) {
        try {
            if (encryptedData == null) return null;

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] decoded = Base64.getDecoder().decode(encryptedData);
            return new String(cipher.doFinal(decoded));

        } catch (Exception ex) {
            throw new RuntimeException("Error decrypting data", ex);
        }
    }
}