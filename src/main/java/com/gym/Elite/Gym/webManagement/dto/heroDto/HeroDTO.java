package com.gym.Elite.Gym.webManagement.dto.heroDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HeroDTO {

    private UUID id;
    private String headline;
    private String subtext;
    private String ctaLabel;
    private String accentPhrase;
    private String backgroundImage;
}
