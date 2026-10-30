package com.hotelbooking.service;

import com.hotelbooking.dto.DashboardStatsDto;
import com.hotelbooking.entity.*;
import com.hotelbooking.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class AdminService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final ProviderRepository providerRepository;
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @jakarta.persistence.PersistenceContext
    private jakarta.persistence.EntityManager entityManager;

    public AdminService(BookingRepository bookingRepository,
                        UserRepository userRepository,
                        ServiceRepository serviceRepository,
                        ProviderRepository providerRepository,
                        RoomRepository roomRepository,
                        HotelRepository hotelRepository,
                        org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.providerRepository = providerRepository;
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public DashboardStatsDto getAdminStats() {
        DashboardStatsDto stats = new DashboardStatsDto();

        long hotelCount = hotelRepository.count();
        if (hotelCount == 0) hotelCount = 1L;
        stats.setTotalHotels(hotelCount);
        stats.setActiveHotels(hotelCount);
        stats.setPendingHotels(0L);

        Hotel defaultHotel = getDefaultHotel();
        long roomCount = defaultHotel != null ? roomRepository.countByHotelId(defaultHotel.getId()) : roomRepository.count();
        stats.setTotalRooms(roomCount);
        stats.setTotalBookings(bookingRepository.count());
        long confirmed = bookingRepository.countByStatus(BookingStatus.CONFIRMED);
        long checkedIn = bookingRepository.countByStatus(BookingStatus.CHECKED_IN);
        long completed = bookingRepository.countByStatus(BookingStatus.COMPLETED);
        stats.setConfirmedBookings(confirmed + checkedIn + completed);
        stats.setPendingBookings(bookingRepository.countByStatus(BookingStatus.PENDING));

        stats.setTotalUsers(userRepository.count());
        stats.setTotalCustomers(userRepository.countByRole(Role.CUSTOMER));

        BigDecimal totalRevUsd = bookingRepository.calculateTotalSystemRevenue();
        if (totalRevUsd == null) totalRevUsd = BigDecimal.ZERO;

        stats.setTotalRevenueUsd(totalRevUsd);
        stats.setTotalRevenueKhr(totalRevUsd.multiply(BigDecimal.valueOf(4100)));

        BigDecimal commission = totalRevUsd.multiply(BigDecimal.valueOf(0.12)).setScale(2, RoundingMode.HALF_UP);
        stats.setTotalCommissionUsd(commission);

        // Rule 6: Revenue today, this week, this month, unpaid balance, and occupancy
        List<Booking> allBookings = bookingRepository.findAll();
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDateTime startOfToday = today.atStartOfDay();
        java.time.LocalDateTime startOfWeek = today.minusDays(7).atStartOfDay();
        java.time.LocalDateTime startOfMonth = today.withDayOfMonth(1).atStartOfDay();

        BigDecimal revToday = BigDecimal.ZERO;
        BigDecimal revWeek = BigDecimal.ZERO;
        BigDecimal revMonth = BigDecimal.ZERO;
        BigDecimal unpaid = BigDecimal.ZERO;

        for (Booking b : allBookings) {
            if (b.getStatus() != BookingStatus.CANCELLED) {
                BigDecimal paid = b.getAmountPaid() != null ? b.getAmountPaid() : (b.getFinalPrice() != null ? b.getFinalPrice() : BigDecimal.ZERO);
                java.time.LocalDateTime created = b.getCreatedAt() != null ? b.getCreatedAt() : java.time.LocalDateTime.now();
                if (!created.isBefore(startOfToday)) {
                    revToday = revToday.add(paid);
                }
                if (!created.isBefore(startOfWeek)) {
                    revWeek = revWeek.add(paid);
                }
                if (!created.isBefore(startOfMonth)) {
                    revMonth = revMonth.add(paid);
                }
                if (b.getBalanceDue() != null && b.getBalanceDue().compareTo(BigDecimal.ZERO) > 0) {
                    unpaid = unpaid.add(b.getBalanceDue());
                }
            }
        }
        stats.setRevenueToday(revToday);
        stats.setRevenueThisWeek(revWeek);
        stats.setRevenueThisMonth(revMonth);
        stats.setUnpaidBalance(unpaid);

        // Occupancy calculation
        List<Room> allRooms = roomRepository.findAll();
        int totalPhysical = allRooms.stream().mapToInt(r -> r.getTotalRooms() != null ? r.getTotalRooms() : 1).sum();
        int totalAvail = allRooms.stream().mapToInt(r -> r.getAvailableUnits() != null ? r.getAvailableUnits() : 0).sum();
        if (totalPhysical > 0) {
            int occupied = Math.max(0, totalPhysical - totalAvail);
            double rate = ((double) occupied / totalPhysical) * 100.0;
            stats.setOccupancyRate(Math.round(rate * 10.0) / 10.0);
        }

        return stats;
    }

    public DashboardStatsDto getOwnerStats(Long ownerOrProviderId) {
        return getAdminStats();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        user.setIsActive(!user.getIsActive());
        return userRepository.save(user);
    }

    @Transactional
    public User approveUser(Long userId, Boolean isApproved) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        user.setIsApproved(isApproved != null ? isApproved : true);
        return userRepository.save(user);
    }

    @Transactional
    public Booking updateBookingStatus(Long bookingId, BookingStatus status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Hotel getDefaultHotel() {
        return hotelRepository.findAll().stream().findFirst().orElse(null);
    }

    @Transactional
    public User createUser(com.hotelbooking.dto.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered: " + request.getEmail());
        }
        if (request.getPhone() != null && !request.getPhone().isBlank() && userRepository.existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException("Phone number is already registered: " + request.getPhone());
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : Role.ADMIN);
        user.setIsActive(true);
        user.setIsApproved(true);
        return userRepository.save(user);
    }

    @Transactional
    public User updateUserRole(Long userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        user.setRole(newRole);
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        if ("admin@booking.com".equalsIgnoreCase(user.getEmail()) || "admin@hotel.com".equalsIgnoreCase(user.getEmail())) {
            throw new IllegalArgumentException("Primary system administrator cannot be deleted.");
        }

        // Safely remove any dependent records before user deletion
        if (entityManager != null) {
            entityManager.createNativeQuery("DELETE FROM notifications WHERE user_id = :uid").setParameter("uid", userId).executeUpdate();
            entityManager.createNativeQuery("DELETE FROM favorites WHERE user_id = :uid").setParameter("uid", userId).executeUpdate();
            entityManager.createNativeQuery("DELETE FROM reviews WHERE customer_id = :uid").setParameter("uid", userId).executeUpdate();
            entityManager.createNativeQuery("UPDATE hotels SET owner_id = NULL WHERE owner_id = :uid").setParameter("uid", userId).executeUpdate();
            entityManager.createNativeQuery("DELETE FROM payments WHERE booking_id IN (SELECT id FROM bookings WHERE user_id = :uid)").setParameter("uid", userId).executeUpdate();
            entityManager.createNativeQuery("DELETE FROM booking_extras WHERE booking_id IN (SELECT id FROM bookings WHERE user_id = :uid)").setParameter("uid", userId).executeUpdate();
            entityManager.createNativeQuery("DELETE FROM booking_items WHERE booking_id IN (SELECT id FROM bookings WHERE user_id = :uid)").setParameter("uid", userId).executeUpdate();
            entityManager.createNativeQuery("DELETE FROM bookings WHERE user_id = :uid").setParameter("uid", userId).executeUpdate();
        }

        userRepository.delete(user);
    }
}
