package com.gym.Elite.Gym.webManagement.repo;

import com.gym.Elite.Gym.webManagement.entity.PromotionalOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PromotionalOfferRepository extends JpaRepository<PromotionalOffer, UUID> {
    List<PromotionalOffer> findByTenantId(UUID tenantId);

    @Query("SELECT p FROM PromotionalOffer p WHERE p.tenantId = :tenantId " +
           "AND p.active = true AND p.publishStatus = 'PUBLISHED' " +
           "AND (p.startDate IS NULL OR p.startDate <= :now) " +
           "AND (p.endDate IS NULL OR p.endDate >= :now) " +
           "AND (p.expiryDate IS NULL OR p.expiryDate >= :now) " +
           "ORDER BY p.displayOrder ASC")
    List<PromotionalOffer> findActiveOffers(UUID tenantId, LocalDateTime now);
}
