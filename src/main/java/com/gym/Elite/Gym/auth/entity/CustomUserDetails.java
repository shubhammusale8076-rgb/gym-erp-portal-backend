package com.gym.Elite.Gym.auth.entity;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

@Getter
public class CustomUserDetails implements UserDetails {

    private final UUID id;
    private final String username;
    private final String password;
    private final UUID tenantId;
    private final Integer tokenVersion;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean enabled;
    private final String roleCode;

    public CustomUserDetails(
            UUID id,
            String username,
            String password,
            UUID tenantId,
            Integer tokenVersion,
            Collection<? extends GrantedAuthority> authorities,
            boolean enabled) {
        this(id, username, password, tenantId, tokenVersion, authorities, enabled, null);
    }

    public CustomUserDetails(
            UUID id,
            String username,
            String password,
            UUID tenantId,
            Integer tokenVersion,
            Collection<? extends GrantedAuthority> authorities,
            boolean enabled,
            String roleCode) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.tenantId = tenantId;
        this.tokenVersion = tokenVersion;
        this.authorities = authorities;
        this.enabled = enabled;
        this.roleCode = roleCode;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
