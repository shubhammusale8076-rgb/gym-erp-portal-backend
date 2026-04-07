package com.gym.Elite.Gym.webManagement.repo;

import com.gym.Elite.Gym.webManagement.entity.Testimonial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TestimonialRepo extends JpaRepository<Testimonial, UUID> {

    List<Testimonial> findByTenant_Id(UUID tenantId);

    List<Testimonial> findByTenant_IdAndIsPublishedTrueAndIsApprovedTrue(UUID tenantId);
}
