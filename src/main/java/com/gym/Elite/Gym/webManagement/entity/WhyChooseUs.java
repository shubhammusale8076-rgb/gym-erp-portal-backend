package com.gym.Elite.Gym.webManagement.entity;

import jakarta.persistence.Column;
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
@Table(name = "why_choose_us")
public class WhyChooseUs extends BaseWebsiteEntity {

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String iconName; // Lucide icon name or similar
}
