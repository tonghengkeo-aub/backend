package com.hotelbooking.service;

import com.hotelbooking.dto.HotelRequest;
import com.hotelbooking.entity.Hotel;
import com.hotelbooking.entity.HotelStatus;
import com.hotelbooking.repository.HotelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;

    public HotelService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public List<Hotel> getActiveHotels() {
        return hotelRepository.findByStatus(HotelStatus.ACTIVE);
    }

    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }

    public Hotel getHotelById(Long id) {
        return hotelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found with ID: " + id));
    }

    public Hotel getHotelByOwnerId(Long ownerId) {
        return hotelRepository.findFirstByOwnerId(ownerId)
                .or(() -> hotelRepository.findFirstByOrderByIdAsc())
                .orElseThrow(() -> new IllegalArgumentException("No hotel registered in system"));
    }

    public List<Hotel> searchHotels(String query, String province) {
        if (province != null && !province.isBlank()) {
            return hotelRepository.findByProvinceAndStatus(province, HotelStatus.ACTIVE);
        }
        if (query != null && !query.isBlank()) {
            return hotelRepository.searchHotels(HotelStatus.ACTIVE, query);
        }
        return getActiveHotels();
    }

    @Transactional
    public Hotel updateHotel(Long hotelId, HotelRequest request) {
        Hotel hotel = getHotelById(hotelId);
        if (request.getName() != null && !request.getName().isBlank()) hotel.setName(request.getName().trim());
        if (request.getTagline() != null) hotel.setTagline(request.getTagline().trim());
        if (request.getStarRating() != null) hotel.setStarRating(request.getStarRating());
        if (request.getDescription() != null) hotel.setDescription(request.getDescription().trim());
        if (request.getAddress() != null) hotel.setAddress(request.getAddress().trim());
        if (request.getProvince() != null) hotel.setProvince(request.getProvince().trim());
        if (request.getPhone() != null) hotel.setPhone(request.getPhone().trim());
        if (request.getEmail() != null) hotel.setEmail(request.getEmail().trim());
        if (request.getMainImage() != null && !request.getMainImage().isBlank()) hotel.setMainImage(request.getMainImage().trim());
        if (request.getFacilities() != null) hotel.setFacilities(request.getFacilities().trim());
        if (request.getMapUrl() != null && !request.getMapUrl().isBlank()) hotel.setMapUrl(request.getMapUrl().trim());
        if (request.getCheckInTime() != null) hotel.setCheckInTime(request.getCheckInTime().trim());
        if (request.getCheckOutTime() != null) hotel.setCheckOutTime(request.getCheckOutTime().trim());
        if (request.getPolicy() != null) hotel.setPolicy(request.getPolicy().trim());
        if (request.getWebsite() != null) hotel.setWebsite(request.getWebsite().trim());
        if (request.getCommissionRate() != null) hotel.setCommissionRate(request.getCommissionRate());

        return hotelRepository.save(hotel);
    }

    @Transactional
    public Hotel updateStatus(Long hotelId, HotelStatus status) {
        Hotel hotel = getHotelById(hotelId);
        hotel.setStatus(status);
        return hotelRepository.save(hotel);
    }
}
