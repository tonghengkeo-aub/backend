package com.hotelbooking.controller;

import com.hotelbooking.dto.CouponResponse;
import com.hotelbooking.dto.CouponValidateRequest;
import com.hotelbooking.entity.Coupon;
import com.hotelbooking.service.CouponService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@CrossOrigin(origins = "*")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping
    public ResponseEntity<List<Coupon>> getActiveCoupons() {
        return ResponseEntity.ok(couponService.getAllActiveCoupons());
    }

    @PostMapping("/validate")
    public ResponseEntity<CouponResponse> validateCoupon(@Valid @RequestBody CouponValidateRequest request) {
        CouponResponse response = couponService.validateCoupon(request.getCode(), request.getSpendAmount());
        return ResponseEntity.ok(response);
    }
}
