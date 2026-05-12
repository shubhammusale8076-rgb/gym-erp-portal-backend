package com.gym.Elite.Gym.webManagement.media;

import com.gym.Elite.Gym.common.entity.TenantAware;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "media_files")
public class MediaFile extends TenantAware {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_url", nullable = false, length = 1000)
    private String fileUrl;

    @Column(name = "file_type")
    private String fileType; // image/png, image/jpeg, etc.

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "alt_text")
    private String altText;

    @Column(name = "thumbnail_url", length = 1000)
    private String thumbnailUrl;

    @Column(name = "category")
    private String category; // HERO, GALLERY, TESTIMONIAL, etc.

    @CreationTimestamp
    @Column(name = "uploaded_at", updatable = false)
    private LocalDateTime uploadedAt;

    @Column(name = "uploaded_by")
    private String uploadedBy;
}
