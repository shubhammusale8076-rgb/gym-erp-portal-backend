package com.gym.Elite.Gym.webManagement.dto.testimonialDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestimonialResponseDTO {

    private UUID id;

    private String clientName;
    private String role;
    private String profileImageUrl;

    private Integer rating;
    private String review;

    private boolean isApproved;
    private boolean isPublished;
    private boolean isFeatured;

    private String source;

    private LocalDateTime createdAt;
}
