package com.shopflow.backend.mapper;

import com.shopflow.backend.dto.request.CouponRequest;
import com.shopflow.backend.dto.response.CouponResponse;
import com.shopflow.backend.entity.Coupon;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CouponMapper {
    CouponResponse toResponse(Coupon coupon);
    List<CouponResponse> toResponseList(List<Coupon> coupons);
    Coupon toEntity(CouponRequest request);
}
