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
@Table(name = "about_section")
public class AboutSection extends BaseWebsiteEntity {

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String imageUrl;
    
    private String experienceYears;
    private String satisfiedClients;
    private String expertTrainers;
}
