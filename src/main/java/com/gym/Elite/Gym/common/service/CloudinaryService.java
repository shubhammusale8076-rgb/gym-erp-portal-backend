package com.gym.Elite.Gym.common.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.gym.Elite.Gym.common.entity.ProfileImageType;
import com.gym.Elite.Gym.tenants.entity.TenantRef;
import com.gym.Elite.Gym.tenants.repo.TenantRefRepository;
import com.gym.Elite.Gym.utility.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;
    private final TenantRefRepository tenantRefRepository;

    public CloudinaryService(Cloudinary cloudinary, TenantRefRepository tenantRefRepository) {
        this.cloudinary = cloudinary;
        this.tenantRefRepository = tenantRefRepository;
    }

    /**
     * Upload profile image for any entity
     * Example folders:
     * gym/members
     * gym/trainers
     * gym/users
     * gym/admins
     */
    public Map<String, String> uploadProfileImage(MultipartFile file, ProfileImageType entityType) {

        System.out.println(cloudinary.config.cloudName);
        System.out.println(cloudinary.config.apiKey);

        UUID tenantId = SecurityUtils.getCurrentTenantId();

        TenantRef tenantRef = tenantRefRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("TenantRef not found"));
        try {

            validateImage(file);

            String tenantSlug = tenantRef.getName()
                    .trim()
                    .toLowerCase()
                    .replaceAll("[^a-z0-9]", "-")
                    .replaceAll("-+", "-");

            String folder =
                    "gym/" +
                            tenantSlug + "-" + tenantId.toString().substring(0, 8)
                            + "/" +
                            entityType.name().toLowerCase();


            String publicId = entityType.name().toLowerCase() + "_" + UUID.randomUUID();

            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder,
                            "public_id", publicId
                    )
            );

            return Map.of(
                    "url", uploadResult.get("secure_url").toString(),
                    "publicId", uploadResult.get("public_id").toString(),
                    "message", "Image uploaded successfully"
            );

        } catch (Exception e) {
            throw new RuntimeException("Image upload failed", e);
        }
    }

    private void validateImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        if (file.getContentType() == null ||
                !file.getContentType().startsWith("image/")) {

            throw new RuntimeException("Only image files allowed");
        }
    }

    public void deleteImage(String publicId) {

        try {

            if (publicId == null || publicId.isBlank()) {
                return;
            }

            cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.emptyMap()
            );

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

        String signature = cloudinary.apiSignRequest(
                paramsToSign,
                cloudinary.config.apiSecret
        );

        return Map.of(
                "timestamp", timestamp,
                "signature", signature,
                "apiKey", cloudinary.config.apiKey,
                "cloudName", cloudinary.config.cloudName,
                "folder", folder
        );
    }
}