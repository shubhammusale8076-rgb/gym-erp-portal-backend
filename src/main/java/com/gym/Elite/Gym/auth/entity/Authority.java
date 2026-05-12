package com.gym.Elite.Gym.auth.entity;

import com.gym.Elite.Gym.common.entity.TenantAware;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;

import java.util.UUID;

@Table(name = "gym_authority",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"tenant_id", "roleCode"})
        })
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Authority extends TenantAware implements GrantedAuthority {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String roleCode;

    @Column(nullable = false)
    private String roleDescription;

    @Override
    public String getAuthority() {

        return roleCode;
    }
}
