package com.gym.Elite.Gym.webManagement.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "social_links")
public class SocialLink extends BaseWebsiteEntity {

    private String platform; // FACEBOOK, INSTAGRAM, TWITTER, LINKEDIN, YOUTUBE
    private String url;
    private String iconName;
}
