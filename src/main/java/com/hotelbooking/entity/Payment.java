package com.hotelbooking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    @JsonIgnore
    private Booking booking;

    @Column(nullable = false, length = 80)
    private String transactionRef;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod method = PaymentMethod.KHQR;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentType paymentType = PaymentType.PAYMENT;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amountUsd;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amountKhr;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status = PaymentStatus.SUCCESS;

    private LocalDateTime paidAt = LocalDateTime.now();
    private LocalDateTime createdAt = LocalDateTime.now();

    public Payment() {}

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
        if (this.paidAt == null) this.paidAt = LocalDateTime.now();
        if (this.status == null) this.status = PaymentStatus.SUCCESS;
        if (this.method == null) this.method = PaymentMethod.KHQR;
        if (this.paymentType == null) this.paymentType = PaymentType.PAYMENT;
        if (this.amountKhr == null && this.amountUsd != null) {
            this.amountKhr = this.amountUsd.multiply(BigDecimal.valueOf(4100));
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }

    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }

    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }

    public PaymentType getPaymentType() { return paymentType != null ? paymentType : PaymentType.PAYMENT; }
    public void setPaymentType(PaymentType paymentType) { this.paymentType = paymentType; }

    public BigDecimal getAmountUsd() { return amountUsd; }
    public void setAmountUsd(BigDecimal amountUsd) { this.amountUsd = amountUsd; }

    public BigDecimal getAmountKhr() { return amountKhr; }
    public void setAmountKhr(BigDecimal amountKhr) { this.amountKhr = amountKhr; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
