package com.hotelbooking.service;

import com.hotelbooking.dto.RoomRequest;
import com.hotelbooking.entity.Hotel;
import com.hotelbooking.entity.Room;
import com.hotelbooking.entity.RoomStatus;
import com.hotelbooking.repository.HotelRepository;
import com.hotelbooking.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;

    public RoomService(RoomRepository roomRepository, HotelRepository hotelRepository) {
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
    }

    public List<Room> getRoomsByHotel(Long hotelId) {
        return roomRepository.findByHotelId(hotelId);
    }

    public List<Room> getAvailableRoomsByHotel(Long hotelId) {
        return roomRepository.findByHotelIdAndStatus(hotelId, RoomStatus.AVAILABLE);
    }

    public Room getRoomById(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + roomId));
    }

    @Transactional
    public Room createRoom(RoomRequest request) {
        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found with ID: " + request.getHotelId()));

        if (roomRepository.existsByHotelIdAndRoomNumber(hotel.getId(), request.getRoomNumber().trim())) {
            throw new IllegalArgumentException("Room number '" + request.getRoomNumber() + "' already exists in this hotel! Please use a unique room number.");
        }

        Room room = new Room();
        room.setHotel(hotel);
        room.setRoomNumber(request.getRoomNumber().trim());
        room.setName(request.getName() != null && !request.getName().isBlank() ? request.getName().trim() : null);
        room.setType(request.getType());
        room.setPriceUsd(request.getPriceUsd());
        room.setPriceKhr(request.getPriceUsd().multiply(BigDecimal.valueOf(4100)));
        room.setMaxGuests(request.getMaxGuests() != null ? request.getMaxGuests() : 2);
        room.setBedType(request.getBedType() != null ? request.getBedType() : "1 King Bed");
        room.setDescription(request.getDescription());
        room.setImages(request.getImages());
        room.setStatus(request.getStatus() != null ? request.getStatus() : RoomStatus.AVAILABLE);
        room.setRating(request.getRating() != null ? request.getRating() : 4.8);
        room.setStarRating(request.getStarRating() != null ? request.getStarRating() : 5);
        room.setReviewCount(request.getReviewCount() != null ? request.getReviewCount() : 128);
        room.setFoodType(request.getFoodType() != null && !request.getFoodType().isBlank() ? request.getFoodType() : "Breakfast included");
        room.setMapUrl(request.getMapUrl() != null && !request.getMapUrl().isBlank() ? request.getMapUrl() : (hotel.getMapUrl() != null ? hotel.getMapUrl() : "https://maps.google.com/?q=Riverside+Blvd+Phnom+Penh"));
        room.setDeluxePriceExtra(request.getDeluxePriceExtra() != null ? request.getDeluxePriceExtra() : BigDecimal.valueOf(30.00));
        room.setPremiumPriceExtra(request.getPremiumPriceExtra() != null ? request.getPremiumPriceExtra() : BigDecimal.valueOf(50.00));
        if (request.getStandardInfo() != null) room.setStandardInfo(request.getStandardInfo());
        if (request.getDeluxeInfo() != null) room.setDeluxeInfo(request.getDeluxeInfo());
        if (request.getPremiumInfo() != null) room.setPremiumInfo(request.getPremiumInfo());
        if (request.getRoomTier() != null) room.setRoomTier(request.getRoomTier());
        if (request.getRoomSize() != null) room.setRoomSize(request.getRoomSize());
        if (request.getAmenities() != null) room.setAmenities(request.getAmenities());
        
        int total = request.getTotalRooms() != null && request.getTotalRooms() > 0 
                ? request.getTotalRooms() 
                : (request.getAvailableUnits() != null ? request.getAvailableUnits() : 1);
        int available = request.getAvailableUnits() != null 
                ? request.getAvailableUnits() 
                : total;
        if (request.getStatus() == RoomStatus.BOOKED) {
            available = 0;
        }
        room.setTotalRooms(total);
        room.setAvailableUnits(available);
        if (available <= 0) {
            room.setStatus(RoomStatus.BOOKED);
        } else if (request.getStatus() != null) {
            room.setStatus(request.getStatus());
        }

        if (request.getFreeCancellation() != null) room.setFreeCancellation(request.getFreeCancellation());
        if (request.getNoPrepayment() != null) room.setNoPrepayment(request.getNoPrepayment());
        if (request.getIncludesBreakfast() != null) room.setIncludesBreakfast(request.getIncludesBreakfast());
        if (request.getIncludesMassage() != null) room.setIncludesMassage(request.getIncludesMassage());

        return roomRepository.save(room);
    }

    @Transactional
    public Room updateRoom(Long roomId, RoomRequest request) {
        Room room = getRoomById(roomId);
        if (request.getRoomNumber() != null && !request.getRoomNumber().trim().equalsIgnoreCase(room.getRoomNumber())) {
            if (roomRepository.existsByHotelIdAndRoomNumberAndIdNot(room.getHotel().getId(), request.getRoomNumber().trim(), roomId)) {
                throw new IllegalArgumentException("Room number '" + request.getRoomNumber() + "' already exists in this hotel! Please use a unique room number.");
            }
            room.setRoomNumber(request.getRoomNumber().trim());
        }
        if (request.getName() != null) {
            room.setName(!request.getName().isBlank() ? request.getName().trim() : null);
        }
        if (request.getType() != null) room.setType(request.getType());
        if (request.getPriceUsd() != null) {
            room.setPriceUsd(request.getPriceUsd());
            room.setPriceKhr(request.getPriceUsd().multiply(BigDecimal.valueOf(4100)));
        }
        if (request.getDeluxePriceExtra() != null) room.setDeluxePriceExtra(request.getDeluxePriceExtra());
        if (request.getPremiumPriceExtra() != null) room.setPremiumPriceExtra(request.getPremiumPriceExtra());
        if (request.getStandardInfo() != null) room.setStandardInfo(request.getStandardInfo());
        if (request.getDeluxeInfo() != null) room.setDeluxeInfo(request.getDeluxeInfo());
        if (request.getPremiumInfo() != null) room.setPremiumInfo(request.getPremiumInfo());
        if (request.getRoomTier() != null) room.setRoomTier(request.getRoomTier());
        if (request.getRoomSize() != null) room.setRoomSize(request.getRoomSize());
        if (request.getAmenities() != null) room.setAmenities(request.getAmenities());
        
        // Rule 1: Inventory logic (Admin adds rooms -> available goes up)
        if (request.getAddQuantity() != null && request.getAddQuantity() > 0) {
            int add = request.getAddQuantity();
            int curTotal = room.getTotalRooms() != null ? room.getTotalRooms() : (room.getAvailableUnits() != null ? room.getAvailableUnits() : 0);
            int curAvail = room.getAvailableUnits() != null ? room.getAvailableUnits() : 0;
            room.setTotalRooms(curTotal + add);
            room.setAvailableUnits(curAvail + add);
        } else if (request.getTotalRooms() != null) {
            int curTotal = room.getTotalRooms() != null ? room.getTotalRooms() : (room.getAvailableUnits() != null ? room.getAvailableUnits() : 0);
            int newTotal = request.getTotalRooms();
            int diff = newTotal - curTotal;
            room.setTotalRooms(newTotal);
            if (diff != 0) {
                int curAvail = room.getAvailableUnits() != null ? room.getAvailableUnits() : 0;
                room.setAvailableUnits(Math.max(0, curAvail + diff));
            } else if (request.getAvailableUnits() != null) {
                room.setAvailableUnits(request.getAvailableUnits());
            }
        } else if (request.getAvailableUnits() != null) {
            room.setAvailableUnits(request.getAvailableUnits());
            if (room.getTotalRooms() == null || room.getTotalRooms() < request.getAvailableUnits()) {
                room.setTotalRooms(request.getAvailableUnits());
            }
        }

        if (room.getAvailableUnits() != null && room.getAvailableUnits() > 0 && room.getStatus() == RoomStatus.BOOKED) {
            room.setStatus(RoomStatus.AVAILABLE);
        } else if (room.getAvailableUnits() != null && room.getAvailableUnits() <= 0) {
            room.setStatus(RoomStatus.BOOKED);
        }

        if (request.getFreeCancellation() != null) room.setFreeCancellation(request.getFreeCancellation());
        if (request.getNoPrepayment() != null) room.setNoPrepayment(request.getNoPrepayment());
        if (request.getIncludesBreakfast() != null) room.setIncludesBreakfast(request.getIncludesBreakfast());
        if (request.getIncludesMassage() != null) room.setIncludesMassage(request.getIncludesMassage());
        if (request.getMaxGuests() != null) room.setMaxGuests(request.getMaxGuests());
        if (request.getBedType() != null) room.setBedType(request.getBedType());
        if (request.getDescription() != null) room.setDescription(request.getDescription());
        if (request.getImages() != null) room.setImages(request.getImages());
        if (request.getRating() != null) room.setRating(request.getRating());
        if (request.getStatus() != null) {
            room.setStatus(request.getStatus());
            if (request.getStatus() == RoomStatus.BOOKED || request.getStatus() == RoomStatus.MAINTENANCE || request.getStatus() == RoomStatus.CLEANING) {
                room.setAvailableUnits(0);
            } else if (request.getStatus() == RoomStatus.AVAILABLE && (room.getAvailableUnits() == null || room.getAvailableUnits() == 0)) {
                room.setAvailableUnits(room.getTotalRooms() != null && room.getTotalRooms() > 0 ? room.getTotalRooms() : 1);
            }
        }
        if (request.getStarRating() != null) room.setStarRating(request.getStarRating());
        if (request.getReviewCount() != null) room.setReviewCount(request.getReviewCount());
        if (request.getFoodType() != null) room.setFoodType(request.getFoodType());
        if (request.getMapUrl() != null) room.setMapUrl(request.getMapUrl());

        return roomRepository.save(room);
    }

    @Transactional
    public void deleteRoom(Long roomId) {
        Room room = getRoomById(roomId);
        roomRepository.delete(room);
    }

    @Transactional
    public Room updateRoomStatus(Long roomId, RoomStatus status) {
        Room room = getRoomById(roomId);
        room.setStatus(status);
        if (status == RoomStatus.AVAILABLE) {
            room.setAvailableUnits(room.getTotalRooms() != null && room.getTotalRooms() > 0 ? room.getTotalRooms() : 1);
        } else if (status == RoomStatus.BOOKED || status == RoomStatus.CLEANING || status == RoomStatus.MAINTENANCE) {
            room.setAvailableUnits(0);
        }
        return roomRepository.save(room);
    }

    @Transactional
    public Room markRoomCleaned(Long roomId) {
        return updateRoomStatus(roomId, RoomStatus.AVAILABLE);
    }
}
