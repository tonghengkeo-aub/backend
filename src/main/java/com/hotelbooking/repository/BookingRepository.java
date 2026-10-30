package com.hotelbooking.repository;

import com.hotelbooking.entity.Booking;
import com.hotelbooking.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingCode(String bookingCode);

    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Booking> findByUserIdOrderByBookingDateDescBookingTimeDesc(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.status IN ('CONFIRMED', 'PENDING', 'RESCHEDULED', 'CHECKED_IN') AND (b.status = 'CHECKED_IN' OR (b.checkOut IS NOT NULL AND b.checkOut >= :today) OR b.bookingDate >= :today) ORDER BY b.bookingDate ASC, b.bookingTime ASC")
    List<Booking> findUpcomingBookings(@Param("userId") Long userId, @Param("today") LocalDate today);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND (b.status IN ('COMPLETED', 'CANCELLED', 'NO_SHOW', 'CHECKED_IN') OR (b.checkOut IS NOT NULL AND b.checkOut < :today) OR (b.checkOut IS NULL AND b.bookingDate < :today)) ORDER BY b.bookingDate DESC, b.bookingTime DESC")
    List<Booking> findHistoryBookings(@Param("userId") Long userId, @Param("today") LocalDate today);

    // Conflict Validation: check if provider already has an active booking at the same date and time
    @Query("SELECT b FROM Booking b WHERE b.provider.id = :providerId AND b.bookingDate = :date AND b.bookingTime = :time AND b.status NOT IN ('CANCELLED')")
    List<Booking> findConflictingBookings(@Param("providerId") Long providerId, @Param("date") LocalDate date, @Param("time") String time);

    // Hotel Room Conflict: Check if room is booked between checkIn and checkOut dates
    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId AND b.status NOT IN ('CANCELLED') AND b.checkIn < :checkOut AND b.checkOut > :checkIn")
    List<Booking> findConflictingRoomBookings(@Param("roomId") Long roomId, @Param("checkIn") LocalDate checkIn, @Param("checkOut") LocalDate checkOut);

    List<Booking> findByHotelIdOrderByCreatedAtDesc(Long hotelId);

    List<Booking> findAllByOrderByCreatedAtDesc();

    long countByStatus(BookingStatus status);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.bookingDate = :today AND b.status NOT IN ('CANCELLED')")
    long countTodayBookings(@Param("today") LocalDate today);

    @Query("SELECT SUM(b.finalPrice) FROM Booking b WHERE b.status IN ('CONFIRMED', 'RESCHEDULED', 'CHECKED_IN', 'COMPLETED')")
    BigDecimal calculateTotalRevenue();

    @Query("SELECT SUM(b.finalPrice) FROM Booking b WHERE b.status IN ('CONFIRMED', 'RESCHEDULED', 'CHECKED_IN', 'COMPLETED')")
    BigDecimal calculateTotalSystemRevenue();

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.provider.id = :providerId")
    long countByProviderId(@Param("providerId") Long providerId);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.provider.id = :providerId AND b.status = :status")
    long countByProviderIdAndStatus(@Param("providerId") Long providerId, @Param("status") BookingStatus status);

    @Query("SELECT SUM(b.finalPrice) FROM Booking b WHERE b.provider.id = :providerId AND b.status IN ('CONFIRMED', 'RESCHEDULED', 'CHECKED_IN', 'COMPLETED')")
    BigDecimal calculateTotalRevenueByProviderId(@Param("providerId") Long providerId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Booking b SET b.room = null WHERE b.room.id = :roomId")
    void detachRoom(@Param("roomId") Long roomId);
}
