package com.gym.Elite.Gym.attendanceEvent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionBookingDTO {
    private UUID sessionId;
    private UUID memberId;

}
