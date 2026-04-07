package com.gym.Elite.Gym.webManagement.service;

import com.gym.Elite.Gym.auth.dto.authDtos.ResponseDto;
import com.gym.Elite.Gym.tenants.entity.Tenants;
import com.gym.Elite.Gym.webManagement.dto.heroDto.HeroDTO;
import com.gym.Elite.Gym.webManagement.entity.HeroSection;
import com.gym.Elite.Gym.webManagement.mapper.HeroSectionMapper;
import com.gym.Elite.Gym.webManagement.repo.HeroSectionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class HeroSectionService {

    private final HeroSectionRepo heroSectionRepo;
    private final HeroSectionMapper heroSectionMapper;

    public HeroDTO get(UUID tenantId) {
        HeroSection hero = heroSectionRepo
                .findByTenant_IdAndStatus(tenantId, "PUBLISHED")
                .orElseThrow(() -> new RuntimeException("Published hero not found"));


        return heroSectionMapper.mapToHeroSectionDTO(hero);
    }

    public ResponseDto saveDraft(UUID tenantId, HeroDTO dto) {
        HeroSection hero = heroSectionRepo.findByTenant_Id(tenantId)
                .orElse(new HeroSection());

        // ⚠️ IMPORTANT: set tenant only when new
        if (hero.getId() == null) {
            Tenants tenant = new Tenants();
            tenant.setId(tenantId);
            hero.setTenant(tenant);
        }

        hero.setHeadline(dto.getHeadline());
        hero.setSubtext(dto.getSubtext());
        hero.setCtaLabel(dto.getCtaLabel());
        hero.setAccentPhrase(dto.getAccentPhrase());
        hero.setBackgroundImage(dto.getBackgroundImage());

        hero.setStatus("DRAFT");
        hero.setUpdatedAt(LocalDateTime.now());

      heroSectionRepo.save(hero);
      return ResponseDto.builder().code(201).message("Draft Saved Successfully").build();
    }

    public ResponseDto publish(UUID tenantId, HeroDTO dto) {

        HeroSection hero = heroSectionRepo.findByTenant_Id(tenantId)
                .orElseGet(HeroSection::new);

        // Set tenant only if new
        if (hero.getId() == null) {
            Tenants tenant = new Tenants();
            tenant.setId(tenantId);
            hero.setTenant(tenant);
        }

        hero.setHeadline(dto.getHeadline());
        hero.setSubtext(dto.getSubtext());
        hero.setCtaLabel(dto.getCtaLabel());
        hero.setAccentPhrase(dto.getAccentPhrase());
        hero.setBackgroundImage(dto.getBackgroundImage());

        hero.setStatus("PUBLISHED");
        hero.setUpdatedAt(LocalDateTime.now());

        heroSectionRepo.save(hero);

        return ResponseDto.builder()
                .code(200)
                .message("Changes published successfully")
                .build();
    }

}
