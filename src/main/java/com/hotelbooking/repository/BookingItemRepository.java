package com.hotelbooking.repository;

import com.hotelbooking.entity.BookingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingItemRepository extends JpaRepository<BookingItem, Long> {
    List<BookingItem> findByBookingId(Long bookingId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE BookingItem bi SET bi.room = null WHERE bi.room.id = :roomId")
    void detachRoom(@Param("roomId") Long roomId);
}
