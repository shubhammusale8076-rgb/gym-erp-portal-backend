package com.gym.Elite.Gym.webManagement.media;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaService {

    private final Cloudinary cloudinary;
    private final MediaFileRepository mediaFileRepository;

    public MediaFile uploadFile(UUID tenantId, MultipartFile file, String category, String altText, String uploadedBy) throws IOException {
        validateFile(file);

        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "folder", "gym_erp/" + tenantId + "/" + category.toLowerCase(),
                "resource_type", "auto"
        ));

        String url = (String) uploadResult.get("secure_url");
        String publicId = (String) uploadResult.get("public_id");
        Long size = ((Number) uploadResult.get("bytes")).longValue();
        String format = (String) uploadResult.get("format");

        MediaFile mediaFile = MediaFile.builder()
                .tenantId(tenantId)
                .fileName(file.getOriginalFilename())
                .fileUrl(url)
                .fileType(file.getContentType())
                .fileSize(size)
                .category(category)
                .altText(altText)
                .uploadedBy(uploadedBy)
                .build();

        return mediaFileRepository.save(mediaFile);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }
        
        // 5MB limit
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("File size exceeds 5MB limit");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Only image files are allowed");
        }
    }

    public void deleteFile(UUID fileId, UUID tenantId) throws IOException {
        MediaFile mediaFile = mediaFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Media file not found"));

        if (!mediaFile.getTenantId().equals(tenantId)) {
            throw new RuntimeException("Access denied to media file");
        }

        // Note: Real deletion from Cloudinary would require storing publicId
        // For simplicity in this implementation, we just remove from DB
        mediaFileRepository.delete(mediaFile);
    }
}
