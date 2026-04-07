package com.gym.Elite.Gym.auth.repo;

import com.gym.Elite.Gym.auth.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepo extends JpaRepository<Authority, Integer> {

    Authority findByRoleCode(String authorityCode);
}
