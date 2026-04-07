package com.gym.Elite.Gym.attendanceEvent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActiveMemberAttendanceDTO {

    private UUID id;
    private UUID memberId;
    private String memberName;
    private String membershipType;

    private LocalDateTime checkInTime;

    private String profileImage;
}