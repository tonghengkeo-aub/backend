package com.hotelbooking.controller;

import com.hotelbooking.dto.FavoriteToggleRequest;
import com.hotelbooking.entity.Room;
import com.hotelbooking.service.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "*")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Long>> getFavoriteRoomIds(@PathVariable Long userId) {
        return ResponseEntity.ok(favoriteService.getFavoriteRoomIds(userId));
    }

    @GetMapping("/rooms/user/{userId}")
    public ResponseEntity<List<Room>> getFavoriteRooms(@PathVariable Long userId) {
        return ResponseEntity.ok(favoriteService.getFavoriteRooms(userId));
    }

    @PostMapping("/toggle")
    public ResponseEntity<Map<String, Object>> toggleFavorite(@RequestBody FavoriteToggleRequest request) {
        if (request.getUserId() == null || request.getRoomId() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(favoriteService.toggleFavorite(request.getUserId(), request.getRoomId()));
    }
}
