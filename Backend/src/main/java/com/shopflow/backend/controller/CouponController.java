package com.shopflow.backend.controller;

import com.shopflow.backend.dto.request.CouponRequest;
import com.shopflow.backend.dto.response.ApiResponse;
import com.shopflow.backend.dto.response.CouponResponse;
import com.shopflow.backend.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CouponResponse>> createCoupon(@Valid @RequestBody CouponRequest request){
        CouponResponse coupon = couponService.createCoupon(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Cupom criado com sucesso", coupon, HttpStatus.CREATED));
    }

    @PreAuthorize(("hasRole('ADMIN')"))
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CouponResponse>>> getAllCoupons(){
        return ResponseEntity.ok(
                ApiResponse.success("Cupons recuperados", couponService.getAllCoupons(), HttpStatus.OK));
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<CouponResponse>> validateCoupon(@RequestParam String code, @RequestParam BigDecimal cartTotal){
        CouponResponse coupon = couponService.validateAndGetCoupon(code, cartTotal);
        return ResponseEntity.ok(
                ApiResponse.success("Cupom válido aplicado", coupon, HttpStatus.OK));
    }
}
