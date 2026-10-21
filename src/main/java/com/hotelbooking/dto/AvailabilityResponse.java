package com.hotelbooking.dto;

import java.time.LocalDate;
import java.util.List;

public class AvailabilityResponse {

    private boolean available;
    private Long providerId;
    private LocalDate date;
    private String requestedTime;
    private List<String> availableSlots;
    private List<String> bookedSlots;
    private String message;

    public AvailabilityResponse() {}

    public AvailabilityResponse(boolean available, Long providerId, LocalDate date, String requestedTime,
                                List<String> availableSlots, List<String> bookedSlots, String message) {
        this.available = available;
        this.providerId = providerId;
        this.date = date;
        this.requestedTime = requestedTime;
        this.availableSlots = availableSlots;
        this.bookedSlots = bookedSlots;
        this.message = message;
    }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public Long getProviderId() { return providerId; }
    public void setProviderId(Long providerId) { this.providerId = providerId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getRequestedTime() { return requestedTime; }
    public void setRequestedTime(String requestedTime) { this.requestedTime = requestedTime; }

    public List<String> getAvailableSlots() { return availableSlots; }
    public void setAvailableSlots(List<String> availableSlots) { this.availableSlots = availableSlots; }

    public List<String> getBookedSlots() { return bookedSlots; }
    public void setBookedSlots(List<String> bookedSlots) { this.bookedSlots = bookedSlots; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
