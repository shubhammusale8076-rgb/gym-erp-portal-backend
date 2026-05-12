package com.gym.Elite.Gym.webManagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class PublishableWebsiteEntity extends BaseWebsiteEntity {

    @Column(name = "publish_status")
    @Builder.Default
    private String publishStatus = "DRAFT"; // DRAFT, PUBLISHED, ARCHIVED

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "soft_delete")
    @Builder.Default
    private Boolean softDelete = false;
}
