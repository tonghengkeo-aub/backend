package com.hotelbooking.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "hotel_settings")
public class HotelSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal taxPercent = new BigDecimal("10.00");

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal serviceChargePercent = new BigDecimal("5.00");

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal depositPercent = new BigDecimal("20.00");

    @Column(nullable = false, length = 10)
    private String currency = "USD";

    @Column(columnDefinition = "TEXT")
    private String cancellationPolicy = "Free cancellation up to 48 hours prior to check-in. Cancellations within 48 hours forfeit the deposit.";

    public HotelSetting() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getTaxPercent() { return taxPercent != null ? taxPercent : new BigDecimal("10.00"); }
    public void setTaxPercent(BigDecimal taxPercent) { this.taxPercent = taxPercent; }

    public BigDecimal getServiceChargePercent() { return serviceChargePercent != null ? serviceChargePercent : new BigDecimal("5.00"); }
    public void setServiceChargePercent(BigDecimal serviceChargePercent) { this.serviceChargePercent = serviceChargePercent; }

    public BigDecimal getDepositPercent() { return depositPercent != null ? depositPercent : new BigDecimal("20.00"); }
    public void setDepositPercent(BigDecimal depositPercent) { this.depositPercent = depositPercent; }

    public String getCurrency() { return currency != null ? currency : "USD"; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getCancellationPolicy() { return cancellationPolicy; }
    public void setCancellationPolicy(String cancellationPolicy) { this.cancellationPolicy = cancellationPolicy; }
}
