package com.gym.Elite.Gym.webManagement.entity;

import com.gym.Elite.Gym.common.entity.TenantAware;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "website_settings")
public class WebsiteSettings extends TenantAware {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Logos
    private String primaryLogo;
    private String footerLogo;
    private String favicon;

    // Footer Info
    @Column(columnDefinition = "TEXT")
    private String footerAboutText;
    private String copyrightText;

    // Contact Quick Info
    private String contactEmail;
    private String contactPhone;
    private String address;

    // Announcement Bar
    @Column(name = "announcement_enabled")
    private Boolean announcementEnabled;
    private String announcementText;
    private String announcementLink;

    // WhatsApp Config
    @Column(name = "whatsapp_enabled")
    private Boolean whatsappEnabled;
    private String whatsappNumber;
    private String whatsappDefaultMessage;

    // Lead Form Config
    @Column(name = "lead_form_enabled")
    private Boolean leadFormEnabled;
    private String leadFormTitle;
}
