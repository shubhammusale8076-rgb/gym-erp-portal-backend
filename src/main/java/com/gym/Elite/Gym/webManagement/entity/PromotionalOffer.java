package com.gym.Elite.Gym.webManagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "promotional_offers")
public class PromotionalOffer extends PublishableWebsiteEntity {

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String discountCode;
    private String imageUrl;
    
    private LocalDateTime expiryDate;
    
    @Column(name = "is_flash_sale")
    private Boolean isFlashSale;
}
