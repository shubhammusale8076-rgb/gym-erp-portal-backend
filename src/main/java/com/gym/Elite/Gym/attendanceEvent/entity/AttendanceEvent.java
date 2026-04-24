package com.gym.Elite.Gym.attendanceEvent.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gym.Elite.Gym.auth.entity.Member;
import com.gym.Elite.Gym.common.entity.TenantAware;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "attendance_events")
public class AttendanceEvent extends TenantAware {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @JsonIgnore
    private Member member;


    private LocalDateTime eventTime;

    private String source; // "RFID", "QR", "MANUAL"

    private String deviceId;

    @Builder.Default
    private boolean processed = false;

    private LocalDateTime createdAt;

}
