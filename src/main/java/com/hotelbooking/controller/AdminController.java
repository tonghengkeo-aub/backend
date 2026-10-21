package com.hotelbooking.controller;

import com.hotelbooking.dto.DashboardStatsDto;
import com.hotelbooking.entity.Booking;
import com.hotelbooking.entity.BookingStatus;
import com.hotelbooking.entity.Hotel;
import com.hotelbooking.entity.HotelStatus;
import com.hotelbooking.entity.Role;
import com.hotelbooking.entity.User;
import com.hotelbooking.service.AdminService;
import com.hotelbooking.service.BookingService;
import com.hotelbooking.service.HotelService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;
    private final HotelService hotelService;
    private final BookingService bookingService;
    private final com.hotelbooking.service.RoomService roomService;

    public AdminController(AdminService adminService,
                           HotelService hotelService,
                           BookingService bookingService,
                           com.hotelbooking.service.RoomService roomService) {
        this.adminService = adminService;
        this.hotelService = hotelService;
        this.bookingService = bookingService;
        this.roomService = roomService;
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDto> getStats() {
        return ResponseEntity.ok(adminService.getAdminStats());
    }

    @GetMapping("/hotel")
    public ResponseEntity<Hotel> getHotel() {
        return ResponseEntity.ok(adminService.getDefaultHotel());
    }

    @PutMapping("/hotel")
    public ResponseEntity<Hotel> updateHotel(@jakarta.validation.Valid @RequestBody com.hotelbooking.dto.HotelRequest request) {
        Hotel defaultHotel = adminService.getDefaultHotel();
        if (defaultHotel == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(hotelService.updateHotel(defaultHotel.getId(), request));
    }

    @GetMapping("/hotels")
    public ResponseEntity<List<Hotel>> getAllHotels() {
        return ResponseEntity.ok(hotelService.getAllHotels());
    }

    @PutMapping("/hotels/{id}/status")
    public ResponseEntity<Hotel> updateHotelStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        HotelStatus status = HotelStatus.valueOf(payload.get("status"));
        return ResponseEntity.ok(hotelService.updateStatus(id, status));
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PutMapping("/users/{id}/toggle-status")
    public ResponseEntity<User> toggleUserStatus(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.toggleUserStatus(id));
    }

    @PutMapping("/users/{id}/approve")
    public ResponseEntity<User> approveUser(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, Boolean> payload) {
        Boolean isApproved = (payload != null && payload.containsKey("isApproved")) ? payload.get("isApproved") : true;
        return ResponseEntity.ok(adminService.approveUser(id, isApproved));
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@jakarta.validation.Valid @RequestBody com.hotelbooking.dto.RegisterRequest request) {
        return ResponseEntity.ok(adminService.createUser(request));
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<User> updateUserRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        Role role = Role.valueOf(payload.get("role"));
        return ResponseEntity.ok(adminService.updateUserRole(id, role));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @PutMapping("/bookings/{id}/status")
    public ResponseEntity<Booking> updateBookingStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        BookingStatus status = BookingStatus.valueOf(payload.get("status"));
        return ResponseEntity.ok(bookingService.updateBookingStatus(id, status));
    }

    @PutMapping("/bookings/{id}/approve")
    public ResponseEntity<Booking> approveBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.updateBookingStatus(id, BookingStatus.CONFIRMED));
    }

    @PutMapping("/bookings/{id}/check-in")
    public ResponseEntity<Booking> checkInBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.updateBookingStatus(id, BookingStatus.CHECKED_IN));
    }

    @PutMapping("/bookings/{id}/check-out")
    public ResponseEntity<Booking> checkOutBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.updateBookingStatus(id, BookingStatus.COMPLETED));
    }

    @PutMapping("/bookings/{id}/complete")
    public ResponseEntity<Booking> completeBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.updateBookingStatus(id, BookingStatus.COMPLETED));
    }

    @PutMapping("/bookings/{id}/reject")
    public ResponseEntity<Booking> rejectBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.updateBookingStatus(id, BookingStatus.CANCELLED));
    }

    @PutMapping("/rooms/{id}/mark-cleaned")
    public ResponseEntity<com.hotelbooking.entity.Room> markRoomCleaned(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.markRoomCleaned(id));
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<com.hotelbooking.entity.Room>> getAllRooms() {
        return ResponseEntity.ok(roomService.getRoomsByHotel(1L));
    }

    @PostMapping("/rooms")
    public ResponseEntity<com.hotelbooking.entity.Room> createRoom(
            @jakarta.validation.Valid @RequestBody com.hotelbooking.dto.RoomRequest request) {
        if (request.getHotelId() == null) {
            Hotel hotel = adminService.getDefaultHotel();
            request.setHotelId(hotel != null ? hotel.getId() : 1L);
        }
        return ResponseEntity.ok(roomService.createRoom(request));
    }

    @PutMapping("/rooms/{id}")
    public ResponseEntity<com.hotelbooking.entity.Room> updateRoom(
            @PathVariable Long id,
            @jakarta.validation.Valid @RequestBody com.hotelbooking.dto.RoomRequest request) {
        return ResponseEntity.ok(roomService.updateRoom(id, request));
    }

    @DeleteMapping("/rooms/{id}")
    public ResponseEntity<Map<String, String>> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.ok(Map.of("message", "Room deleted successfully"));
    }
}
