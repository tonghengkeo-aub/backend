package com.hotelbooking.service;

import com.hotelbooking.entity.Favorite;
import com.hotelbooking.entity.Room;
import com.hotelbooking.entity.User;
import com.hotelbooking.repository.FavoriteRepository;
import com.hotelbooking.repository.RoomRepository;
import com.hotelbooking.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    public FavoriteService(FavoriteRepository favoriteRepository,
                           UserRepository userRepository,
                           RoomRepository roomRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
    }

    public List<Long> getFavoriteRoomIds(Long userId) {
        return favoriteRepository.findByUserId(userId)
                .stream()
                .map(fav -> fav.getRoom().getId())
                .collect(Collectors.toList());
    }

    public List<Room> getFavoriteRooms(Long userId) {
        return favoriteRepository.findByUserId(userId)
                .stream()
                .map(Favorite::getRoom)
                .collect(Collectors.toList());
    }

    @Transactional
    public Map<String, Object> toggleFavorite(Long userId, Long roomId) {
        Map<String, Object> response = new HashMap<>();

        Optional<Favorite> existing = favoriteRepository.findByUserIdAndRoomId(userId, roomId);
        boolean added;
        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
            added = false;
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new RuntimeException("Room not found with id: " + roomId));
            Favorite favorite = new Favorite(user, room);
            favoriteRepository.save(favorite);
            added = true;
        }

        List<Long> currentIds = getFavoriteRoomIds(userId);
        response.put("isFavorite", added);
        response.put("userId", userId);
        response.put("roomId", roomId);
        response.put("favoriteRoomIds", currentIds);
        response.put("count", currentIds.size());
        return response;
    }
}
