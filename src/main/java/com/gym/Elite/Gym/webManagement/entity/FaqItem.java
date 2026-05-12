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
@Table(name = "faq_items")
public class FaqItem extends BaseWebsiteEntity {

    private String question;

    @Column(columnDefinition = "TEXT")
    private String answer;
}
