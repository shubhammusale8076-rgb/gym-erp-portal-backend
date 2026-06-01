package com.gym.Elite.Gym.auth.repo;

import com.gym.Elite.Gym.auth.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissionRepo extends JpaRepository<Permission, UUID> {

    Optional<Permission> findByPermissionCode(String permissionCode);

    List<Permission> findByPermissionCodeIn(Collection<String> permissionCodes);
}
