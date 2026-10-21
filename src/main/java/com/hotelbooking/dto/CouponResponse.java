package com.hotelbooking.dto;

import java.math.BigDecimal;

public class CouponResponse {

    private boolean valid;
    private String code;
    private Integer discountPercent;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String message;

    public CouponResponse() {}

    public CouponResponse(boolean valid, String code, Integer discountPercent, BigDecimal discountAmount, BigDecimal finalAmount, String message) {
        this.valid = valid;
        this.code = code;
        this.discountPercent = discountPercent;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.message = message;
    }

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Integer getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(Integer discountPercent) { this.discountPercent = discountPercent; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getFinalAmount() { return finalAmount; }
    public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
