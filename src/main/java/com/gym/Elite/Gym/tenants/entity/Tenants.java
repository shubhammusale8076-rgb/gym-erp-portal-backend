package com.gym.Elite.Gym.tenants.entity;


import com.gym.Elite.Gym.auth.entity.Member;
import com.gym.Elite.Gym.auth.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(unique = true)
    private String email;

    private String phoneNumber;

    private String plan;

    private Boolean status;

    private Date createdOn;

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users  = new ArrayList<>();

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Member> members  = new ArrayList<>();

//    @OneToMany(mappedBy = "tenant")
//    private List<Lead> leads;

}
