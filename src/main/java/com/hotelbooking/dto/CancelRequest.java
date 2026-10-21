package com.hotelbooking.dto;

import jakarta.validation.constraints.NotBlank;

public class CancelRequest {

    @NotBlank(message = "Cancellation reason is required")
    private String reason;

    public CancelRequest() {}

    public CancelRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
