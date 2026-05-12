package com.gym.Elite.Gym.trainer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerMemberDTO {

    private UUID id;
    private String fullName;
    private String email;
    private String programName;     // plan
    private LocalDateTime nextSession;
    private String progressStatus;  // ON_TRACK, WARNING, etc.
}
