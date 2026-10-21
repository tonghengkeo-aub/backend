package com.hotelbooking.dto;

public class FavoriteToggleRequest {
    private Long userId;
    private Long roomId;

    public FavoriteToggleRequest() {
    }

    public FavoriteToggleRequest(Long userId, Long roomId) {
        this.userId = userId;
        this.roomId = roomId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }
}
