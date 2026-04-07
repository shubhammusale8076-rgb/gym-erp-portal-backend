package com.gym.Elite.Gym.webManagement.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public Map<String, Object> generateUploadSignature(UUID tenantId) {

        long timestamp = System.currentTimeMillis() / 1000;

        String folder = "gym/" + tenantId + "/gallery";

        Map<String, Object> paramsToSign = ObjectUtils.asMap(
                "timestamp", timestamp,
                "folder", folder
        );

        String signature = cloudinary.apiSignRequest(paramsToSign,
                cloudinary.config.apiSecret);

        return Map.of(
                "timestamp", timestamp,
                "signature", signature,
                "apiKey", cloudinary.config.apiKey,
                "cloudName", cloudinary.config.cloudName,
                "folder", folder
        );
    }
}
