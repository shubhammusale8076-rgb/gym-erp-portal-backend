package com.gym.Elite.Gym.trainer.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gym.Elite.Gym.common.entity.TenantAware;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "trainer_availability")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TrainerAvailability extends TenantAware {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "trainer_id")
    @JsonIgnore
    private Trainer trainer;

    // MON, TUE, etc.
    private String dayOfWeek;

    // Store as string "HH:mm" (your current approach is fine)
    private String startTime;
    private String endTime;

    private Boolean active;

    @CreationTimestamp
    private LocalDateTime createdOn;
}