package com.hotelbooking.controller;

import com.hotelbooking.dto.HotelRequest;
import com.hotelbooking.entity.Hotel;
import com.hotelbooking.service.HotelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
@CrossOrigin(origins = "*")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping
    public ResponseEntity<List<Hotel>> getHotels(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String province) {
        if ((query != null && !query.isBlank()) || (province != null && !province.isBlank())) {
            return ResponseEntity.ok(hotelService.searchHotels(query, province));
        }
        return ResponseEntity.ok(hotelService.getActiveHotels());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hotel> getHotelById(@PathVariable Long id) {
        return ResponseEntity.ok(hotelService.getHotelById(id));
    }
}
