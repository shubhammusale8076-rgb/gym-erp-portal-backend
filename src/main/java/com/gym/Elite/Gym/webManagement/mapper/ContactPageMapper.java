package com.gym.Elite.Gym.webManagement.mapper;

import com.gym.Elite.Gym.tenants.entity.Tenants;
import com.gym.Elite.Gym.webManagement.dto.contactUsDto.ContactDTO;
import com.gym.Elite.Gym.webManagement.dto.contactUsDto.ContactPageDTO;
import com.gym.Elite.Gym.webManagement.dto.contactUsDto.OperatingHoursDto;
import com.gym.Elite.Gym.webManagement.entity.ContactInfo;
import com.gym.Elite.Gym.webManagement.entity.OperatingHours;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class ContactPageMapper {

    public ContactPageDTO mapToContactDTO(ContactInfo contact, List<OperatingHours> hours) {

        ContactPageDTO dto = new ContactPageDTO();

        // Contact
        if (contact != null) {
            ContactDTO contactDTO = new ContactDTO();

            contactDTO.setSupportEmail(contact.getSupportEmail());
            contactDTO.setPhoneNumber(contact.getPhoneNumber());
            contactDTO.setEmergencyContact(contact.getEmergencyContact());
            contactDTO.setAddress(contact.getAddress());

            contactDTO.setInstagram(contact.getInstagram());
            contactDTO.setTwitter(contact.getTwitter());
            contactDTO.setFacebook(contact.getFacebook());
            contactDTO.setYoutube(contact.getYoutube());

            dto.setContact(contactDTO);
        } else {
            dto.setContact(new ContactDTO());
        }

        // Hours
        List<OperatingHoursDto> hoursDTO = hours.stream().map(h -> {
            OperatingHoursDto o = new OperatingHoursDto();
            o.setDayOfWeek(h.getDayOfWeek());
            o.setIsClosed(h.getIsClosed());

            if (!Boolean.TRUE.equals(h.getIsClosed())) {
                o.setOpenTime(h.getOpenTime().toString());
                o.setCloseTime(h.getCloseTime().toString());
            }

            return o;
        }).toList();

        dto.setOperatingHours(hoursDTO);

        return dto;
    }

    public void mapContact(ContactInfo entity, ContactDTO dto) {

        entity.setSupportEmail(dto.getSupportEmail());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setEmergencyContact(dto.getEmergencyContact());
        entity.setAddress(dto.getAddress());

        entity.setInstagram(dto.getInstagram());
        entity.setTwitter(dto.getTwitter());
        entity.setFacebook(dto.getFacebook());
        entity.setYoutube(dto.getYoutube());

        entity.setUpdatedAt(LocalDateTime.now());
    }
}
