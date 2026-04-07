package com.gym.Elite.Gym.webManagement.mapper;

import com.gym.Elite.Gym.webManagement.dto.testimonialDTO.TestimonialResponseDTO;
import com.gym.Elite.Gym.webManagement.entity.Testimonial;
import org.springframework.stereotype.Component;

@Component
public class TestimonialMapper {

    public TestimonialResponseDTO mapToResponse(Testimonial t) {
        return TestimonialResponseDTO.builder()
                .id(t.getId())
                .clientName(t.getClientName())
                .role(t.getRole())
                .profileImageUrl(t.getProfileImageUrl())
                .rating(t.getRating())
                .review(t.getReview())
                .isApproved(t.isApproved())
                .isPublished(t.isPublished())
                .isFeatured(t.isFeatured())
                .source(t.getSource())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
