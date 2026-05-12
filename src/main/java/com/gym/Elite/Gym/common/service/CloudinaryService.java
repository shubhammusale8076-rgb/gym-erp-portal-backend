package com.gym.Elite.Gym.common.service;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    // 🔥 Upload Image
    public Map<String, String> uploadImage(MultipartFile file) {
        try {

            // ✅ Basic validation
            if (file == null || file.isEmpty()) {
                throw new RuntimeException("File is empty");
            }

            if (!file.getContentType().startsWith("image/")) {
                throw new RuntimeException("Only image files allowed");
            }

            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "gym/members",
                            "public_id", "member_" + UUID.randomUUID()
                    )
            );

            return Map.of(
                    "url", uploadResult.get("secure_url").toString(),
                    "publicId", uploadResult.get("public_id").toString(),
                    "message","Images Uploaded Successfully"
            );

        } catch (Exception e) {
            throw new RuntimeException("Image upload failed", e);
        }
    }

    // 🔥 Delete Image
    public void deleteImage(String publicId) {
        try {
            if (publicId == null || publicId.isBlank()) return;

            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

        } catch (Exception e) {
            throw new RuntimeException("Image deletion failed", e);
        }
    }

    public Map<String, Object> generateUploadSignature(UUID tenantId) {

        long timestamp = Instant.now().getEpochSecond();


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
