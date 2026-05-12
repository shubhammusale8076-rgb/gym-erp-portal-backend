package com.gym.Elite.Gym.auth.repo;

import com.gym.Elite.Gym.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {
    User findByEmailAndTenantId(String email, UUID tenantId);
    List<User> findByTenantId(UUID tenantId);

    User findByEmail(String username);

    List<User> findByTenantIdAndEnabled(UUID tenantId, boolean enabled);
}
