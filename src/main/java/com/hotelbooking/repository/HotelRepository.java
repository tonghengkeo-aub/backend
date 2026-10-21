package com.hotelbooking.repository;

import com.hotelbooking.entity.Hotel;
import com.hotelbooking.entity.HotelStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    List<Hotel> findByStatus(HotelStatus status);
    List<Hotel> findByOwnerId(Long ownerId);
    Optional<Hotel> findFirstByOwnerId(Long ownerId);
    Optional<Hotel> findFirstByOrderByIdAsc();

    @Query("SELECT h FROM Hotel h WHERE h.status = :status AND " +
           "(LOWER(h.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(h.province) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(h.address) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Hotel> searchHotels(@Param("status") HotelStatus status, @Param("query") String query);

    @Query("SELECT h FROM Hotel h WHERE h.status = :status AND LOWER(h.province) = LOWER(:province)")
    List<Hotel> findByProvinceAndStatus(@Param("province") String province, @Param("status") HotelStatus status);

    long countByStatus(HotelStatus status);
}
