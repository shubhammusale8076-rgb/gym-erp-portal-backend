package com.gym.Elite.Gym.attendanceEvent.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gym.Elite.Gym.auth.entity.Member;
import com.gym.Elite.Gym.tenants.entity.Tenants;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "attendance_event")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceEvent {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    @JsonIgnore
    private Member member;

    private String gymId;

    private LocalDateTime eventTime;

    private String source; // BIOMETRIC / MANUAL / API

    private String deviceId;
    private String externalId;

    private Boolean processed = false;

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    @JsonIgnore
    private Tenants tenant;
}
