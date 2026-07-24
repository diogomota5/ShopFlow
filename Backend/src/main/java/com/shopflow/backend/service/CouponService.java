package com.shopflow.backend.service;

import com.shopflow.backend.dto.request.CouponRequest;
import com.shopflow.backend.dto.response.CouponResponse;
import com.shopflow.backend.entity.Coupon;
import com.shopflow.backend.entity.enums.DiscountType;
import com.shopflow.backend.exception.BusinessRuleException;
import com.shopflow.backend.mapper.CouponMapper;
import com.shopflow.backend.repository.CouponRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponMapper couponMapper;

    @Transactional
    public CouponResponse createCoupon(CouponRequest request){
        couponRepository.findByCodeIgnoreCase(request.code())
                .ifPresent(coupon -> {throw new BusinessRuleException("Já existe um cupom com esse código");
                });

        Coupon coupon = couponMapper.toEntity(request);
        return couponMapper.toResponse(couponRepository.save(coupon));
    }

    @Transactional(readOnly = true)
    public List<CouponResponse> getAllCoupons(){
        return couponMapper.toResponseList(couponRepository.findAll());
    }

    @Transactional(readOnly = true)
    public CouponResponse validateAndGetCoupon(String code, BigDecimal cartTotal){
        Coupon coupon = couponRepository.findByCodeIgnoreCase(code.trim())
                .orElseThrow(() -> new EntityNotFoundException("Cupom não encontrado: " + code));

        if(!coupon.isValid(cartTotal)){
            throw new BusinessRuleException("Este cupom é inválido, expirou ou não atingiu o valor mínimo de compra");
        }

        return couponMapper.toResponse(coupon);
    }

    public BigDecimal calculateDiscount(Coupon coupon, BigDecimal cartTotal){
        if (coupon.getDiscountType() == DiscountType.FIXED){
            return coupon.getDiscountValue().min(cartTotal);
        } else {
            return cartTotal.multiply(coupon.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
    }

}
