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
@Table(name = "transformation_records")
public class TransformationRecord extends BaseWebsiteEntity {

    private String memberName;
    
    @Column(name = "before_image_url")
    private String beforeImageUrl;
    
    @Column(name = "after_image_url")
    private String afterImageUrl;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String duration; // e.g. "3 Months"
    private String weightLost; // e.g. "15 kg"
}
