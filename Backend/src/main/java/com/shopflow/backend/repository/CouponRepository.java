package com.shopflow.backend.repository;

import com.shopflow.backend.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, UUID> {
    Optional<Coupon> findByCodeIgnoreCase(String code);

    @Modifying
    @Query("update Coupon c set c.usedCount = c.usedCount + 1 where c.id = :couponId and c.active = true and (c.maxUses = 0 or c.usedCount < c.maxUses)")
    int consumeIfAvailable(@Param("couponId") UUID couponId);
}
