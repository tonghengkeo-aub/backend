package com.hotelbooking.repository;

import com.hotelbooking.entity.Room;
import com.hotelbooking.entity.RoomStatus;
import com.hotelbooking.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByHotelId(Long hotelId);
    List<Room> findByHotelIdAndStatus(Long hotelId, RoomStatus status);
    List<Room> findByHotelIdAndType(Long hotelId, RoomType type);
    long countByHotelId(Long hotelId);
    long countByHotelIdAndStatus(Long hotelId, RoomStatus status);
    boolean existsByHotelIdAndRoomNumber(Long hotelId, String roomNumber);
    boolean existsByHotelIdAndRoomNumberAndIdNot(Long hotelId, String roomNumber, Long id);

    @Modifying
    @Query(value = "UPDATE rooms SET available_units = available_units - :units, status = IF(available_units - :units <= 0, 'BOOKED', 'AVAILABLE') WHERE id = :id AND available_units >= :units", nativeQuery = true)
    int deductRoomInventory(@Param("id") Long id, @Param("units") int units);

    @Modifying
    @Query(value = "UPDATE rooms SET available_units = LEAST(IFNULL(total_rooms, available_units + :units), available_units + :units), status = 'AVAILABLE' WHERE id = :id", nativeQuery = true)
    int restoreRoomInventory(@Param("id") Long id, @Param("units") int units);
}
