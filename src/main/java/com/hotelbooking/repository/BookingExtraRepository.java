package com.hotelbooking.repository;

import com.hotelbooking.entity.BookingExtra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingExtraRepository extends JpaRepository<BookingExtra, Long> {
    List<BookingExtra> findByBookingId(Long bookingId);
}
