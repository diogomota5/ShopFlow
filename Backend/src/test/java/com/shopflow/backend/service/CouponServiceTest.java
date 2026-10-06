package com.shopflow.backend.service;

import com.shopflow.backend.entity.Coupon;
import com.shopflow.backend.exception.BusinessRuleException;
import com.shopflow.backend.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    private Coupon validCoupon;

    @BeforeEach
    void setUp() {
        validCoupon = Coupon.builder()
                .code("PROMO10")
                .discountPercentage(BigDecimal.valueOf(10))
                .active(true)
                .usedCount(0)
                .usageLimit(100)
                .minPurchaseValue(BigDecimal.valueOf(100))
                .validUntil(LocalDateTime.now().plusDays(10))
                .build();
    }

    @Test
    void calculateDiscount_Success() {
        BigDecimal subtotal = BigDecimal.valueOf(200);
        BigDecimal discount = couponService.calculateDiscount(validCoupon, subtotal);
        assertEquals(0, discount.compareTo(BigDecimal.valueOf(20))); // 10% de 200 = 20
    }

    @Test
    void validateCoupon_Expired() {
        validCoupon.setValidUntil(LocalDateTime.now().minusDays(1));
        boolean isValid = validCoupon.isValid(BigDecimal.valueOf(200));
        assertEquals(false, isValid);
    }
}
