package com.hotelbooking.controller;

import com.hotelbooking.dto.DashboardStatsDto;
import com.hotelbooking.dto.HotelRequest;
import com.hotelbooking.dto.RoomRequest;
import com.hotelbooking.entity.Booking;
import com.hotelbooking.entity.BookingStatus;
import com.hotelbooking.entity.Hotel;
import com.hotelbooking.entity.Room;
import com.hotelbooking.security.CustomUserDetails;
import com.hotelbooking.service.AdminService;
import com.hotelbooking.service.BookingService;
import com.hotelbooking.service.HotelService;
import com.hotelbooking.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/owner")
@PreAuthorize("hasAnyRole('HOTEL_OWNER', 'ADMIN')")
@CrossOrigin(origins = "*")
public class OwnerController {

    private final HotelService hotelService;
    private final RoomService roomService;
    private final BookingService bookingService;
    private final AdminService adminService;

    public OwnerController(HotelService hotelService,
                           RoomService roomService,
                           BookingService bookingService,
                           AdminService adminService) {
        this.hotelService = hotelService;
        this.roomService = roomService;
        this.bookingService = bookingService;
        this.adminService = adminService;
    }

    @GetMapping("/my-hotel")
    public ResponseEntity<Hotel> getMyHotel(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(hotelService.getHotelByOwnerId(userDetails.getId()));
    }

    @PutMapping("/my-hotel")
    public ResponseEntity<Hotel> updateMyHotel(
            @Valid @RequestBody HotelRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Hotel myHotel = hotelService.getHotelByOwnerId(userDetails.getId());
        return ResponseEntity.ok(hotelService.updateHotel(myHotel.getId(), request));
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDto> getOwnerStats(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Hotel myHotel = hotelService.getHotelByOwnerId(userDetails.getId());
        return ResponseEntity.ok(adminService.getOwnerStats(myHotel.getId()));
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<Room>> getMyRooms(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Hotel myHotel = hotelService.getHotelByOwnerId(userDetails.getId());
        return ResponseEntity.ok(roomService.getRoomsByHotel(myHotel.getId()));
    }

    @PostMapping("/rooms")
    public ResponseEntity<Room> createRoom(
            @Valid @RequestBody RoomRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Hotel myHotel = hotelService.getHotelByOwnerId(userDetails.getId());
        request.setHotelId(myHotel.getId());
        return ResponseEntity.ok(roomService.createRoom(request));
    }

    @PutMapping("/rooms/{id}")
    public ResponseEntity<Room> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody RoomRequest request) {
        return ResponseEntity.ok(roomService.updateRoom(id, request));
    }

    @DeleteMapping("/rooms/{id}")
    public ResponseEntity<Map<String, String>> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.ok(Map.of("message", "Room deleted successfully"));
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getMyHotelBookings(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Hotel myHotel = hotelService.getHotelByOwnerId(userDetails.getId());
        return ResponseEntity.ok(bookingService.getHotelBookings(myHotel.getId()));
    }

    @PutMapping("/bookings/{id}/status")
    public ResponseEntity<Booking> updateBookingStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        BookingStatus status = BookingStatus.valueOf(payload.get("status"));
        return ResponseEntity.ok(bookingService.updateBookingStatus(id, status));
    }
}
