package com.gym.Elite.Gym.attendanceEvent.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class SessionBookingDTO {

    private UUID sessionId;
    private UUID memberId;
}
