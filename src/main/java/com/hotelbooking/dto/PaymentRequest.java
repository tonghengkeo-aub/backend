package com.hotelbooking.dto;

import com.hotelbooking.entity.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public class PaymentRequest {
    @NotNull
    private Long bookingId;

    @NotNull
    private PaymentMethod method = PaymentMethod.KHQR;

    private String transactionRef;

    public PaymentRequest() {}

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }

    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }
}
