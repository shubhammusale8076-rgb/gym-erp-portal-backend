package com.gym.Elite.Gym.webManagement.mapper;

import com.gym.Elite.Gym.webManagement.dto.heroDto.HeroDTO;
import com.gym.Elite.Gym.webManagement.entity.HeroSection;
import org.springframework.stereotype.Component;

@Component
public class HeroSectionMapper {

    public HeroDTO mapToHeroSectionDTO(HeroSection hero) {
        HeroDTO dto = new HeroDTO();
        dto.setId(hero.getId());
        dto.setHeadline(hero.getHeadline());
        dto.setSubtext(hero.getSubtext());
        dto.setCtaLabel(hero.getCtaLabel());
        dto.setAccentPhrase(hero.getAccentPhrase());
        dto.setBackgroundImage(hero.getBackgroundImage());
        return dto;
    }
}
