package com.hotelbooking.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PriceCalculationResponse {

    private int nights = 1;
    private int quantity = 1;
    private BigDecimal pricePerNight = BigDecimal.ZERO;
    private BigDecimal roomTotal = BigDecimal.ZERO;
    private BigDecimal extrasTotal = BigDecimal.ZERO;
    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal discount = BigDecimal.ZERO;
    private String couponCode;
    private BigDecimal serviceCharge = BigDecimal.ZERO;
    private BigDecimal serviceChargePercent = BigDecimal.ZERO;
    private BigDecimal tax = BigDecimal.ZERO;
    private BigDecimal taxPercent = BigDecimal.ZERO;
    private BigDecimal total = BigDecimal.ZERO;
    private BigDecimal totalKhr = BigDecimal.ZERO;
    private BigDecimal depositPercent = BigDecimal.ZERO;
    private BigDecimal depositRequired = BigDecimal.ZERO;
    private BigDecimal balanceDue = BigDecimal.ZERO;
    private String cancellationPolicy;

    public PriceCalculationResponse() {}

    public int getNights() { return nights; }
    public void setNights(int nights) { this.nights = nights; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; }

    public BigDecimal getRoomTotal() { return roomTotal; }
    public void setRoomTotal(BigDecimal roomTotal) { this.roomTotal = roomTotal; }

    public BigDecimal getExtrasTotal() { return extrasTotal; }
    public void setExtrasTotal(BigDecimal extrasTotal) { this.extrasTotal = extrasTotal; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }

    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }

    public BigDecimal getServiceCharge() { return serviceCharge; }
    public void setServiceCharge(BigDecimal serviceCharge) { this.serviceCharge = serviceCharge; }

    public BigDecimal getServiceChargePercent() { return serviceChargePercent; }
    public void setServiceChargePercent(BigDecimal serviceChargePercent) { this.serviceChargePercent = serviceChargePercent; }

    public BigDecimal getTax() { return tax; }
    public void setTax(BigDecimal tax) { this.tax = tax; }

    public BigDecimal getTaxPercent() { return taxPercent; }
    public void setTaxPercent(BigDecimal taxPercent) { this.taxPercent = taxPercent; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public BigDecimal getTotalKhr() { return totalKhr; }
    public void setTotalKhr(BigDecimal totalKhr) { this.totalKhr = totalKhr; }

    public BigDecimal getDepositPercent() { return depositPercent; }
    public void setDepositPercent(BigDecimal depositPercent) { this.depositPercent = depositPercent; }

    public BigDecimal getDepositRequired() { return depositRequired; }
    public void setDepositRequired(BigDecimal depositRequired) { this.depositRequired = depositRequired; }

    public BigDecimal getBalanceDue() { return balanceDue; }
    public void setBalanceDue(BigDecimal balanceDue) { this.balanceDue = balanceDue; }

    public String getCancellationPolicy() { return cancellationPolicy; }
    public void setCancellationPolicy(String cancellationPolicy) { this.cancellationPolicy = cancellationPolicy; }
}
