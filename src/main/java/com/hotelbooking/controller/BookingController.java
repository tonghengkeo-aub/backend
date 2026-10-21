package com.hotelbooking.controller;

import com.hotelbooking.dto.*;
import com.hotelbooking.entity.Booking;
import com.hotelbooking.entity.BookingStatus;
import com.hotelbooking.entity.HotelSetting;
import com.hotelbooking.entity.PaymentMethod;
import com.hotelbooking.entity.PaymentType;
import com.hotelbooking.security.CustomUserDetails;
import com.hotelbooking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // Rule 2 & 5: Return price breakdown before customer confirms
    @PostMapping("/calculate")
    public ResponseEntity<PriceCalculationResponse> calculatePrice(@RequestBody PriceCalculationRequest request) {
        return ResponseEntity.ok(bookingService.calculatePrice(request));
    }

    @PostMapping
    public ResponseEntity<Booking> createBooking(
            @Valid @RequestBody BookingRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Booking booking = bookingService.createBooking(request, userDetails != null ? userDetails.getUser() : null);
        return ResponseEntity.ok(booking);
    }

    // Rule 3: Support deposit first, then remaining balance at check-in / check-out
    @PostMapping("/{id}/payments")
    public ResponseEntity<Booking> recordPayment(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload) {
        BigDecimal amount = new BigDecimal(payload.get("amount").toString());
        PaymentMethod method = payload.get("method") != null
                ? PaymentMethod.valueOf(payload.get("method").toString().toUpperCase())
                : PaymentMethod.KHQR;
        PaymentType type = payload.get("type") != null
                ? PaymentType.valueOf(payload.get("type").toString().toUpperCase())
                : PaymentType.PAYMENT;
        return ResponseEntity.ok(bookingService.recordPayment(id, amount, method, type));
    }

    @GetMapping("/settings")
    public ResponseEntity<HotelSetting> getHotelSettings() {
        return ResponseEntity.ok(bookingService.getHotelSetting());
    }

    @PutMapping("/settings")
    public ResponseEntity<HotelSetting> updateHotelSettings(@RequestBody HotelSetting setting) {
        return ResponseEntity.ok(bookingService.updateHotelSetting(setting));
    }

    @GetMapping("/my")
    public ResponseEntity<List<Booking>> getMyBookings(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(bookingService.getMyBookings(userDetails.getId()));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<Booking>> getUpcomingBookings(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(bookingService.getUpcomingBookings(userDetails.getId()));
    }

    @GetMapping("/history")
    public ResponseEntity<List<Booking>> getHistoryBookings(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(bookingService.getHistoryBookings(userDetails.getId()));
    }

    @GetMapping("/check-availability")
    public ResponseEntity<AvailabilityResponse> checkAvailability(
            @RequestParam(required = false) Long serviceId,
            @RequestParam Long providerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String time) {
        return ResponseEntity.ok(bookingService.checkAvailability(serviceId, providerId, date, time));
    }

    @PutMapping("/{id}/reschedule")
    public ResponseEntity<Booking> rescheduleBooking(
            @PathVariable Long id,
            @Valid @RequestBody RescheduleRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Booking updated = bookingService.rescheduleBooking(id, request, userDetails.getUser());
        return ResponseEntity.ok(updated);
    }

    // Rule 4: Cancel and refund
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Booking> cancelBooking(
            @PathVariable Long id,
            @Valid @RequestBody CancelRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Booking updated = bookingService.cancelBooking(id, request, userDetails.getUser());
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<Booking> getBookingByCode(@PathVariable String code) {
        return ResponseEntity.ok(bookingService.getBookingByCode(code));
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Booking> updateBookingStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        BookingStatus status = BookingStatus.valueOf(payload.get("status"));
        return ResponseEntity.ok(bookingService.updateBookingStatus(id, status));
    }
}
