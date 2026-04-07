package com.gym.Elite.Gym.webManagement.dto.testimonialDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestimonialDTO {

    private String clientName;
    private String role;

    private String profileImageUrl;

    private Integer rating; // 1–5

    private String review;

    private String source;

    // optional control from UI
    private Boolean publishNow; // true → publish immediately
    private Boolean featured;   // mark as featured
}