package com.gym.Elite.Gym.crm.specification;

import com.gym.Elite.Gym.crm.entity.Lead;
import com.gym.Elite.Gym.crm.enums.LeadSource;
import com.gym.Elite.Gym.crm.enums.LeadStage;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LeadSpecification {

    private LeadSpecification() {}

    /**
     * Build a composite specification for the lead list API.
     * All criteria are AND-combined and always scoped to the tenant.
     */
    public static Specification<Lead> buildFilter(
            UUID tenantId,
            String search,
            LeadStage stage,
            LeadSource source,
            UUID assignedTo
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // ── Mandatory: tenant isolation + soft-delete filter ──────────────
            predicates.add(cb.equal(root.get("tenantId"), tenantId));
            predicates.add(cb.equal(root.get("deleted"), false));

            // ── Optional: full-text search across name / phone / email ────────
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase().trim() + "%";
                Predicate nameLike  = cb.like(cb.lower(root.get("fullName")), pattern);
                Predicate phoneLike = cb.like(cb.lower(root.get("phone")),    pattern);
                Predicate emailLike = cb.like(cb.lower(root.get("email")),    pattern);
                predicates.add(cb.or(nameLike, phoneLike, emailLike));
            }

            // ── Optional: stage filter ────────────────────────────────────────
            if (stage != null) {
                predicates.add(cb.equal(root.get("stage"), stage));
            }

            // ── Optional: source filter ───────────────────────────────────────
            if (source != null) {
                predicates.add(cb.equal(root.get("source"), source));
            }

            // ── Optional: assigned-to filter ──────────────────────────────────
            if (assignedTo != null) {
                predicates.add(cb.equal(root.get("assignedTo"), assignedTo));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
