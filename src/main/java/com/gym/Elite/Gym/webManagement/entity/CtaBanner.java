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
@Table(name = "cta_banners")
public class CtaBanner extends BaseWebsiteEntity {

    private String title;
    private String subtitle;
    private String buttonText;
    private String buttonLink;
    private String backgroundImage;
}
