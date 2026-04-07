package com.gym.Elite.Gym.auth.mapper;

import com.gym.Elite.Gym.auth.dto.memberDto.MemberResponseDTO;
import com.gym.Elite.Gym.auth.entity.Member;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {

    public MemberResponseDTO mapToDTO(Member member) {
        return MemberResponseDTO.builder()
                .id(member.getId())
                .fullName(member.getFullName())
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .active(member.getActive())
                .createdOn(member.getCreatedOn())
                .build();
    }
}
