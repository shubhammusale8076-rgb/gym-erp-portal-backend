package com.gym.Elite.Gym.tenants.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gym.Elite.Gym.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tenants")
public class Tenants {

    @Id
    @GeneratedValue
    private UUID id;

    private String gymName;

    private String ownerName;

    private String email;

    private String phoneNumber;

    private String address;

    private String plan; // BASIC, PREMIUM, ENTERPRISE

    private boolean status;

    private Date createdOn;

    private Date updatedOn;

    @Builder.Default
    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<User> users = new ArrayList<>();

}
