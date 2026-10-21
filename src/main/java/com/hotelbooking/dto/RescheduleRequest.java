package com.hotelbooking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class RescheduleRequest {

    @NotNull(message = "New booking date is required")
    private LocalDate newDate;

    @NotBlank(message = "New booking time is required")
    private String newTime;

    private String reason;

    public RescheduleRequest() {}

    public RescheduleRequest(LocalDate newDate, String newTime, String reason) {
        this.newDate = newDate;
        this.newTime = newTime;
        this.reason = reason;
    }

    public LocalDate getNewDate() { return newDate; }
    public void setNewDate(LocalDate newDate) { this.newDate = newDate; }

    public String getNewTime() { return newTime; }
    public void setNewTime(String newTime) { this.newTime = newTime; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
