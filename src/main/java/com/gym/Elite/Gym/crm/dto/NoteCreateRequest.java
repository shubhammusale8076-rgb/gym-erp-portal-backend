package com.gym.Elite.Gym.crm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteCreateRequest {

    @NotBlank(message = "Note text is required")
    private String note;
}
