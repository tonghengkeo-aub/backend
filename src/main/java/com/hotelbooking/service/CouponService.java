package com.hotelbooking.service;

import com.hotelbooking.dto.CouponResponse;
import com.hotelbooking.entity.Coupon;
import com.hotelbooking.repository.CouponRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public List<Coupon> getAllActiveCoupons() {
        return couponRepository.findAll().stream()
                .filter(c -> Boolean.TRUE.equals(c.getIsActive()))
                .toList();
    }

    public CouponResponse validateCoupon(String code, BigDecimal spendAmount) {
        if (code == null || code.trim().isEmpty()) {
            return new CouponResponse(false, code, 0, BigDecimal.ZERO, spendAmount, "Coupon code cannot be empty");
        }

        String cleanCode = code.trim().toUpperCase();
        Optional<Coupon> opt = couponRepository.findByCodeIgnoreCaseAndIsActiveTrue(cleanCode);

        if (opt.isEmpty()) {
            return new CouponResponse(false, cleanCode, 0, BigDecimal.ZERO, spendAmount, "Invalid coupon code");
        }

        Coupon coupon = opt.get();
        if (coupon.getExpiresAt() != null && coupon.getExpiresAt().isBefore(LocalDate.now())) {
            return new CouponResponse(false, cleanCode, 0, BigDecimal.ZERO, spendAmount, "Coupon has expired");
        }

        if (coupon.getMinSpend() != null && spendAmount.compareTo(coupon.getMinSpend()) < 0) {
            return new CouponResponse(false, cleanCode, 0, BigDecimal.ZERO, spendAmount,
                    "Minimum spend for this coupon is $" + coupon.getMinSpend());
        }

        BigDecimal discount = BigDecimal.ZERO;
        if (coupon.getDiscountPercent() != null && coupon.getDiscountPercent() > 0) {
            discount = spendAmount.multiply(BigDecimal.valueOf(coupon.getDiscountPercent()))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else if (coupon.getDiscountAmount() != null && coupon.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            discount = coupon.getDiscountAmount();
        }

        BigDecimal finalAmount = spendAmount.subtract(discount).max(BigDecimal.ZERO);

        return new CouponResponse(true, cleanCode, coupon.getDiscountPercent(), discount, finalAmount,
                "Coupon applied successfully! You saved $" + discount);
    }
}
