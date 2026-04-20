package com.gym.Elite.Gym.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gym.Elite.Gym.tenants.entity.Tenants;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue
    private UUID id;

    private String fullName;

    @JsonIgnore
    private String password;

    private Date createdOn;

    private Date updatedOn;

    @Column(nullable = false,unique = true)
    private String email;

    private String phoneNumber;

    @Builder.Default
    private boolean enabled = false;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    @JsonIgnore
    private Tenants tenant;

    @ManyToOne
    @JoinColumn(name = "authority_id" )
    private Authority authority;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        if (authority == null) return List.of();

        Set<GrantedAuthority> authorities = new HashSet<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_" +  authority.getRoleCode()));

        return authorities;
    }


    @Override
    public String getPassword() {

        return this.password;
    }

    @Override
    public String getUsername() {

        return this.email;
    }

}
