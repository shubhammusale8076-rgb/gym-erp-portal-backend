package com.gym.Elite.Gym.crm.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteResponseDto {

    private UUID id;
    private UUID leadId;
    private String note;
    private String createdBy;
    private LocalDateTime createdAt;
}
