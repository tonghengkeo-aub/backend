package com.hotelbooking.dto;

import com.hotelbooking.entity.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingRequest {

    private Long serviceId;
    private Long providerId;
    private Long locationId;

    private LocalDate bookingDate;
    private String bookingTime = "14:00";

    @NotBlank(message = "Guest name is required")
    private String guestName;

    @NotBlank(message = "Guest phone is required")
    private String guestPhone;

    private String guestEmail;
    private String notes;
    private String couponCode;
    private PaymentMethod paymentMethod = PaymentMethod.KHQR;

    private Long hotelId;
    private Long roomId;
    private Integer numberOfRooms = 1;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private String specialRequests;

    // Hotel calculation extras & deposit options
    private List<PriceCalculationRequest.ExtraItemDto> extras = new ArrayList<>();
    private Boolean payDepositOnly = false;

    public BookingRequest() {}

    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }

    public Long getProviderId() { return providerId; }
    public void setProviderId(Long providerId) { this.providerId = providerId; }

    public Long getLocationId() { return locationId; }
    public void setLocationId(Long locationId) { this.locationId = locationId; }

    public LocalDate getBookingDate() {
        return bookingDate != null ? bookingDate : (checkIn != null ? checkIn : LocalDate.now());
    }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }

    public String getBookingTime() {
        return bookingTime != null && !bookingTime.trim().isEmpty() ? bookingTime : "14:00";
    }
    public void setBookingTime(String bookingTime) { this.bookingTime = bookingTime; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getGuestPhone() { return guestPhone; }
    public void setGuestPhone(String guestPhone) { this.guestPhone = guestPhone; }

    public String getGuestEmail() { return guestEmail; }
    public void setGuestEmail(String guestEmail) { this.guestEmail = guestEmail; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public Long getHotelId() { return hotelId; }
    public void setHotelId(Long hotelId) { this.hotelId = hotelId; }

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public Integer getNumberOfRooms() { return numberOfRooms != null && numberOfRooms > 0 ? numberOfRooms : 1; }
    public void setNumberOfRooms(Integer numberOfRooms) { this.numberOfRooms = numberOfRooms; }

    public LocalDate getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDate checkIn) { this.checkIn = checkIn; }

    public LocalDate getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDate checkOut) { this.checkOut = checkOut; }

    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }

    public List<PriceCalculationRequest.ExtraItemDto> getExtras() { return extras != null ? extras : new ArrayList<>(); }
    public void setExtras(List<PriceCalculationRequest.ExtraItemDto> extras) { this.extras = extras; }

    public Boolean getPayDepositOnly() { return payDepositOnly != null ? payDepositOnly : false; }
    public void setPayDepositOnly(Boolean payDepositOnly) { this.payDepositOnly = payDepositOnly; }
}
