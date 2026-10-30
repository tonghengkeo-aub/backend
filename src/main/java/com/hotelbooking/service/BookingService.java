package com.hotelbooking.service;

import com.hotelbooking.dto.*;
import com.hotelbooking.entity.*;
import com.hotelbooking.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private static final List<String> STANDARD_TIME_SLOTS = List.of(
            "08:30 AM", "09:30 AM", "10:30 AM", "11:30 AM",
            "01:30 PM", "02:30 PM", "03:30 PM", "04:30 PM", "05:30 PM", "06:30 PM"
    );

    private final BookingRepository bookingRepository;
    private final ServiceRepository serviceRepository;
    private final ProviderRepository providerRepository;
    private final LocationRepository locationRepository;
    private final CouponRepository couponRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationRepository notificationRepository;
    private final ActivityLogRepository activityLogRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final BookingItemRepository bookingItemRepository;
    private final BookingExtraRepository bookingExtraRepository;
    private final HotelSettingRepository hotelSettingRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository,
                          ServiceRepository serviceRepository,
                          ProviderRepository providerRepository,
                          LocationRepository locationRepository,
                          CouponRepository couponRepository,
                          PaymentRepository paymentRepository,
                          NotificationRepository notificationRepository,
                          ActivityLogRepository activityLogRepository,
                          HotelRepository hotelRepository,
                          RoomRepository roomRepository,
                          BookingItemRepository bookingItemRepository,
                          BookingExtraRepository bookingExtraRepository,
                          HotelSettingRepository hotelSettingRepository,
                          UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.serviceRepository = serviceRepository;
        this.providerRepository = providerRepository;
        this.locationRepository = locationRepository;
        this.couponRepository = couponRepository;
        this.paymentRepository = paymentRepository;
        this.notificationRepository = notificationRepository;
        this.activityLogRepository = activityLogRepository;
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
        this.bookingItemRepository = bookingItemRepository;
        this.bookingExtraRepository = bookingExtraRepository;
        this.hotelSettingRepository = hotelSettingRepository;
        this.userRepository = userRepository;
    }

    public HotelSetting getHotelSetting() {
        HotelSetting setting = hotelSettingRepository.findAll().stream().findFirst().orElseGet(() -> {
            HotelSetting s = new HotelSetting();
            return hotelSettingRepository.save(s);
        });
        // Ensure tax and service charge reflect the updated 1% rule if still on old defaults (10% or 5%)
        boolean updated = false;
        if (setting.getTaxPercent() == null || setting.getTaxPercent().compareTo(BigDecimal.valueOf(10.00)) == 0) {
            setting.setTaxPercent(BigDecimal.valueOf(1.00));
            updated = true;
        }
        if (setting.getServiceChargePercent() == null || setting.getServiceChargePercent().compareTo(BigDecimal.valueOf(5.00)) == 0) {
            setting.setServiceChargePercent(BigDecimal.valueOf(1.00));
            updated = true;
        }
        if (updated) {
            hotelSettingRepository.save(setting);
        }
        return setting;
    }

    public HotelSetting updateHotelSetting(HotelSetting setting) {
        HotelSetting current = getHotelSetting();
        if (setting.getTaxPercent() != null) current.setTaxPercent(setting.getTaxPercent());
        if (setting.getServiceChargePercent() != null) current.setServiceChargePercent(setting.getServiceChargePercent());
        if (setting.getDepositPercent() != null) current.setDepositPercent(setting.getDepositPercent());
        if (setting.getCancellationPolicy() != null) current.setCancellationPolicy(setting.getCancellationPolicy());
        return hotelSettingRepository.save(current);
    }

    // ==========================================
    // BACKEND PRICE CALCULATION (Rule 2 & 5)
    // ==========================================
    public PriceCalculationResponse calculatePrice(PriceCalculationRequest request) {
        HotelSetting setting = getHotelSetting();

        int nights = 1;
        if (request.getCheckIn() != null && request.getCheckOut() != null) {
            long diff = ChronoUnit.DAYS.between(request.getCheckIn(), request.getCheckOut());
            nights = diff > 0 ? (int) diff : 1;
        }

        int quantity = request.getQuantity();

        BigDecimal pricePerNight = BigDecimal.valueOf(75.00);
        if (request.getRoomId() != null) {
            Optional<Room> roomOpt = roomRepository.findById(request.getRoomId());
            if (roomOpt.isPresent() && roomOpt.get().getPriceUsd() != null && roomOpt.get().getPriceUsd().compareTo(BigDecimal.ZERO) > 0) {
                pricePerNight = roomOpt.get().getPriceUsd();
            }
        }

        BigDecimal roomTotal = pricePerNight.multiply(BigDecimal.valueOf(nights)).multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);

        BigDecimal extrasTotal = BigDecimal.ZERO;
        if (request.getExtras() != null) {
            for (PriceCalculationRequest.ExtraItemDto extra : request.getExtras()) {
                if (extra.getPrice() != null && extra.getQuantity() != null && extra.getQuantity() > 0) {
                    BigDecimal line = extra.getPrice().multiply(BigDecimal.valueOf(extra.getQuantity()));
                    extrasTotal = extrasTotal.add(line);
                }
            }
        }
        extrasTotal = extrasTotal.setScale(2, RoundingMode.HALF_UP);

        BigDecimal subtotal = roomTotal.add(extrasTotal).setScale(2, RoundingMode.HALF_UP);

        BigDecimal discount = BigDecimal.ZERO;
        if (request.getCouponCode() != null && !request.getCouponCode().trim().isEmpty()) {
            String code = request.getCouponCode().trim().toUpperCase();
            Optional<Coupon> couponOpt = couponRepository.findByCodeIgnoreCaseAndIsActiveTrue(code);
            if (couponOpt.isPresent()) {
                Coupon coupon = couponOpt.get();
                if (coupon.getExpiresAt() == null || !coupon.getExpiresAt().isBefore(LocalDate.now())) {
                    if (coupon.getMinSpend() == null || subtotal.compareTo(coupon.getMinSpend()) >= 0) {
                        if (coupon.getDiscountPercent() != null && coupon.getDiscountPercent() > 0) {
                            discount = subtotal.multiply(BigDecimal.valueOf(coupon.getDiscountPercent()))
                                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                        } else if (coupon.getDiscountAmount() != null && coupon.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
                            discount = coupon.getDiscountAmount();
                        }
                    }
                }
            }
        }
        discount = discount.min(subtotal).setScale(2, RoundingMode.HALF_UP);

        BigDecimal netAfterDiscount = subtotal.subtract(discount).max(BigDecimal.ZERO);

        BigDecimal serviceChargePercent = setting.getServiceChargePercent();
        BigDecimal serviceCharge = netAfterDiscount.multiply(serviceChargePercent)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal taxPercent = setting.getTaxPercent();
        BigDecimal tax = (netAfterDiscount.add(serviceCharge)).multiply(taxPercent)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal total = netAfterDiscount.add(serviceCharge).add(tax).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalKhr = total.multiply(BigDecimal.valueOf(4100)).setScale(2, RoundingMode.HALF_UP);

        BigDecimal depositPercent = setting.getDepositPercent();
        BigDecimal depositRequired = total.multiply(depositPercent)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal balanceDue = total.subtract(depositRequired).max(BigDecimal.ZERO);

        PriceCalculationResponse resp = new PriceCalculationResponse();
        resp.setNights(nights);
        resp.setQuantity(quantity);
        resp.setPricePerNight(pricePerNight);
        resp.setRoomTotal(roomTotal);
        resp.setExtrasTotal(extrasTotal);
        resp.setSubtotal(subtotal);
        resp.setDiscount(discount);
        resp.setCouponCode(request.getCouponCode());
        resp.setServiceCharge(serviceCharge);
        resp.setServiceChargePercent(serviceChargePercent);
        resp.setTax(tax);
        resp.setTaxPercent(taxPercent);
        resp.setTotal(total);
        resp.setTotalKhr(totalKhr);
        resp.setDepositPercent(depositPercent);
        resp.setDepositRequired(depositRequired);
        resp.setBalanceDue(balanceDue);
        resp.setCancellationPolicy(setting.getCancellationPolicy());
        return resp;
    }

    public AvailabilityResponse checkAvailability(Long serviceId, Long providerId, LocalDate date, String requestedTime) {
        if (date == null) {
            date = LocalDate.now();
        }
        if (date.isBefore(LocalDate.now())) {
            return new AvailabilityResponse(false, providerId, date, requestedTime,
                    Collections.emptyList(), Collections.emptyList(), "Cannot check availability for past dates");
        }

        List<Booking> existingBookings = bookingRepository.findAll();
        Set<String> bookedSlots = new HashSet<>();
        for (Booking b : existingBookings) {
            if (b.getProvider() != null && Objects.equals(b.getProvider().getId(), providerId)
                    && date.equals(b.getBookingDate())
                    && b.getStatus() != BookingStatus.CANCELLED) {
                bookedSlots.add(b.getBookingTime());
            }
        }

        List<String> availableSlots = new ArrayList<>();
        for (String slot : STANDARD_TIME_SLOTS) {
            if (!bookedSlots.contains(slot)) {
                availableSlots.add(slot);
            }
        }

        boolean isRequestedAvailable = requestedTime == null || availableSlots.contains(requestedTime);
        String msg = isRequestedAvailable
                ? "Time slot is available for booking"
                : "Selected time slot is already booked. Please choose an available time.";

        return new AvailabilityResponse(isRequestedAvailable, providerId, date, requestedTime,
                availableSlots, new ArrayList<>(bookedSlots), msg);
    }

    // ==========================================
    // CREATE BOOKING (ONE TRANSACTION - Rule 5)
    // ==========================================
    @Transactional
    public Booking createBooking(BookingRequest request, User customer) {
        LocalDate checkInDate = request.getCheckIn() != null ? request.getCheckIn() : request.getBookingDate();
        if (checkInDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Booking check-in date cannot be in the past.");
        }

        Long serviceId = request.getServiceId() != null ? request.getServiceId() : 1L;
        ServiceEntity service = serviceRepository.findById(serviceId).orElse(null);

        Provider provider = null;
        if (request.getProviderId() != null) {
            provider = providerRepository.findById(request.getProviderId()).orElse(null);
        } else if (service != null && !service.getProviders().isEmpty()) {
            provider = service.getProviders().get(0);
        } else {
            provider = providerRepository.findAll().stream().findFirst().orElse(null);
        }

        Location location = null;
        if (request.getLocationId() != null) {
            location = locationRepository.findById(request.getLocationId()).orElse(null);
        } else if (service != null && service.getLocation() != null) {
            location = service.getLocation();
        } else {
            location = locationRepository.findAll().stream().findFirst().orElse(null);
        }

        int requestedUnits = request.getNumberOfRooms();

        Room bookedRoom = null;
        Hotel bookedHotel = null;
        if (request.getRoomId() != null) {
            bookedRoom = roomRepository.findById(request.getRoomId())
                    .orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + request.getRoomId()));
            bookedHotel = bookedRoom.getHotel();

            // Find all rooms in this hotel matching this room type and tier
            final Room targetRoom = bookedRoom;
            List<Room> sameTierRooms = roomRepository.findByHotelId(bookedHotel.getId()).stream()
                    .filter(r -> r.getType() == targetRoom.getType() && Objects.equals(r.getRoomTier(), targetRoom.getRoomTier()))
                    .collect(Collectors.toList());

            int totalTierAvailable = sameTierRooms.stream()
                    .mapToInt(r -> r.getAvailableUnits() != null ? r.getAvailableUnits() : 0)
                    .sum();

            if (totalTierAvailable <= 0) {
                throw new IllegalStateException("Sorry, this room is completely sold out!");
            }
            if (requestedUnits > totalTierAvailable) {
                throw new IllegalStateException("Sorry, only " + totalTierAvailable + " room" + (totalTierAvailable == 1 ? "" : "s") + " left.");
            }

            // Deduct requestedUnits across available rooms of this tier atomically with DB row locking
            int remainingToDeduct = requestedUnits;
            int bookedRoomAvail = bookedRoom.getAvailableUnits() != null ? bookedRoom.getAvailableUnits() : 0;
            if (bookedRoomAvail > 0) {
                int deductFromThis = Math.min(remainingToDeduct, bookedRoomAvail);
                int rows = roomRepository.deductRoomInventory(bookedRoom.getId(), deductFromThis);
                if (rows > 0) {
                    remainingToDeduct -= deductFromThis;
                }
            }

            if (remainingToDeduct > 0) {
                for (Room other : sameTierRooms) {
                    if (!other.getId().equals(bookedRoom.getId())) {
                        int otherAvail = other.getAvailableUnits() != null ? other.getAvailableUnits() : 0;
                        if (otherAvail > 0) {
                            int deductFromThis = Math.min(remainingToDeduct, otherAvail);
                            int rows = roomRepository.deductRoomInventory(other.getId(), deductFromThis);
                            if (rows > 0) {
                                remainingToDeduct -= deductFromThis;
                                if (remainingToDeduct <= 0) break;
                            }
                        }
                    }
                }
            }

            if (remainingToDeduct > 0) {
                throw new IllegalStateException("Sorry, only " + (requestedUnits - remainingToDeduct) + " room" + ((requestedUnits - remainingToDeduct) == 1 ? "" : "s") + " left.");
            }
        } else if (request.getHotelId() != null) {
            bookedHotel = hotelRepository.findById(request.getHotelId()).orElse(null);
        }

        // Calculate strictly on backend
        PriceCalculationRequest calcReq = new PriceCalculationRequest();
        calcReq.setRoomId(request.getRoomId());
        calcReq.setQuantity(requestedUnits);
        calcReq.setCheckIn(request.getCheckIn());
        calcReq.setCheckOut(request.getCheckOut());
        calcReq.setCouponCode(request.getCouponCode());
        calcReq.setExtras(request.getExtras());

        PriceCalculationResponse calc = calculatePrice(calcReq);

        boolean isCash = request.getPaymentMethod() == PaymentMethod.CASH;
        boolean payDepositOnly = Boolean.TRUE.equals(request.getPayDepositOnly());

        // For online/KHQR payment, booking starts UNPAID/PENDING until customer actually scans and pays.
        // For CASH, booking starts UNPAID/CONFIRMED (payable upon arrival at hotel).
        BigDecimal amountPaid = BigDecimal.ZERO;
        BigDecimal balanceDue = calc.getTotal();
        String paymentStatus = "UNPAID";
        BookingStatus initialStatus = isCash ? BookingStatus.CONFIRMED : BookingStatus.PENDING;

        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomSuffix = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        String bookingCode = "BK-" + dateStr + "-" + randomSuffix;

        if (customer == null) {
            final String guestPhone = request.getGuestPhone();
            if (guestPhone != null && !guestPhone.trim().isEmpty()) {
                customer = userRepository.findByPhone(guestPhone).orElse(null);
            }
            if (customer == null) {
                customer = userRepository.findAll().stream()
                        .filter(u -> u.getRole() == Role.CUSTOMER)
                        .findFirst()
                        .orElse(null);
            }
        }

        Booking booking = new Booking();
        booking.setBookingCode(bookingCode);
        booking.setUser(customer);
        booking.setHotel(bookedHotel);
        booking.setRoom(bookedRoom);
        booking.setNumberOfRooms(requestedUnits);
        booking.setCheckIn(request.getCheckIn());
        booking.setCheckOut(request.getCheckOut());
        booking.setTotalNights(calc.getNights());
        booking.setPaymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod().name() : "KHQR");
        booking.setPaymentStatus(paymentStatus);
        booking.setService(service);
        booking.setProvider(provider);
        booking.setLocation(location);
        booking.setBookingDate(checkInDate);
        booking.setBookingTime(request.getBookingTime());
        booking.setDurationMinutes(service != null ? service.getDurationMinutes() : 1440);

        booking.setSubtotal(calc.getSubtotal());
        booking.setTotalPrice(calc.getSubtotal());
        booking.setDiscountAmount(calc.getDiscount());
        booking.setServiceCharge(calc.getServiceCharge());
        booking.setTax(calc.getTax());
        booking.setFinalPrice(calc.getTotal());
        booking.setFinalPriceKhr(calc.getTotalKhr());
        booking.setDepositRequired(calc.getDepositRequired());
        booking.setAmountPaid(amountPaid);
        booking.setBalanceDue(balanceDue);

        booking.setCouponCode(request.getCouponCode());
        booking.setGuestName(request.getGuestName() != null ? request.getGuestName() : (customer != null ? customer.getFullName() : "Guest Customer"));
        booking.setGuestPhone(request.getGuestPhone() != null ? request.getGuestPhone() : (customer != null ? customer.getPhone() : ""));
        booking.setGuestEmail(request.getGuestEmail() != null ? request.getGuestEmail() : (customer != null ? customer.getEmail() : null));
        booking.setNotes(request.getNotes() != null ? request.getNotes() : request.getSpecialRequests());
        booking.setStatus(initialStatus);

        Booking savedBooking = bookingRepository.save(booking);

        // 1. Save Booking Item Snapshot (Rule 5)
        if (bookedRoom != null) {
            BookingItem item = new BookingItem(
                    savedBooking,
                    bookedRoom,
                    bookedRoom.getName() != null && !bookedRoom.getName().isEmpty() ? bookedRoom.getName() : "Room " + bookedRoom.getRoomNumber(),
                    bookedRoom.getType() != null ? bookedRoom.getType().name() : "SINGLE",
                    bookedRoom.getRoomTier(),
                    requestedUnits,
                    calc.getPricePerNight(),
                    calc.getNights(),
                    calc.getRoomTotal()
            );
            bookingItemRepository.save(item);
            savedBooking.getItems().add(item);
        }

        // 2. Save Booking Extras (Rule 1 & 2)
        if (request.getExtras() != null) {
            for (PriceCalculationRequest.ExtraItemDto extra : request.getExtras()) {
                if (extra.getPrice() != null && extra.getQuantity() != null && extra.getQuantity() > 0) {
                    BigDecimal line = extra.getPrice().multiply(BigDecimal.valueOf(extra.getQuantity()));
                    BookingExtra be = new BookingExtra(savedBooking, extra.getServiceName(), extra.getPrice(), extra.getQuantity(), line);
                    bookingExtraRepository.save(be);
                    savedBooking.getExtras().add(be);
                }
            }
        }

        // Note: For online/KHQR, payment record is NOT created here. It is recorded only when payment is actually received!

        // Create Real In-App Notification
        Notification notification = new Notification();
        notification.setUser(customer);
        if (isCash) {
            notification.setTitle("Booking Reserved #" + bookingCode);
            notification.setMessage("Your booking #" + bookingCode + " is confirmed. Pay US$" + calc.getTotal() + " in cash upon arrival at the hotel.");
            notification.setType("BOOKING_CONFIRMED");
        } else {
            notification.setTitle("Payment Required #" + bookingCode);
            notification.setMessage("Booking #" + bookingCode + " initiated. Total: US$" + calc.getTotal() + ". Please scan Bakong KHQR (sen_tongheng@bkrt) to complete payment.");
            notification.setType("BOOKING_PENDING");
        }
        notificationRepository.save(notification);

        // Activity Log
        ActivityLog log = new ActivityLog(customer != null ? customer.getId() : 1L, "BOOKING_CREATED",
                "Created booking #" + bookingCode + " (" + (isCash ? "Cash at Hotel" : "Awaiting KHQR Payment") + ") for room " + (bookedRoom != null ? bookedRoom.getName() : "Hotel Room"));
        activityLogRepository.save(log);

        return savedBooking;
    }

    // ==========================================
    // RECORD ADDITIONAL PAYMENT (Rule 3)
    // ==========================================
    @Transactional
    public Booking recordPayment(Long bookingId, BigDecimal amount, PaymentMethod method, PaymentType type) {
        Booking booking = getBookingById(bookingId);
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        }

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setTransactionRef("TXN-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        payment.setMethod(method != null ? method : PaymentMethod.KHQR);
        payment.setPaymentType(type != null ? type : PaymentType.PAYMENT);
        payment.setAmountUsd(amount);
        payment.setAmountKhr(amount.multiply(BigDecimal.valueOf(4100)));
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);

        BigDecimal currentPaid = booking.getAmountPaid() != null ? booking.getAmountPaid() : BigDecimal.ZERO;
        BigDecimal newPaid = currentPaid.add(amount);
        booking.setAmountPaid(newPaid);

        BigDecimal total = booking.getFinalPrice() != null ? booking.getFinalPrice() : BigDecimal.ZERO;
        BigDecimal newBalance = total.subtract(newPaid).max(BigDecimal.ZERO);
        booking.setBalanceDue(newBalance);

        if (newBalance.compareTo(BigDecimal.ZERO) == 0) {
            booking.setPaymentStatus("PAID");
            booking.setStatus(BookingStatus.CONFIRMED);
            booking.setCancellationReason(null);
        } else {
            booking.setPaymentStatus("DEPOSIT");
            booking.setStatus(BookingStatus.CONFIRMED);
            booking.setCancellationReason(null);
        }

        // Notification for successful payment
        try {
            if (booking.getUser() != null) {
                Notification notif = new Notification();
                notif.setUser(booking.getUser());
                notif.setTitle("Payment Received #" + booking.getBookingCode());
                notif.setMessage("Payment of US$" + amount + " received via Bakong KHQR (sen_tongheng@bkrt). Your booking is now confirmed!");
                notif.setType("PAYMENT_SUCCESS");
                notificationRepository.save(notif);
            }
        } catch (Exception ignored) {}

        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking rescheduleBooking(Long bookingId, RescheduleRequest request, User currentUser) {
        Booking booking = getBookingById(bookingId);

        if (currentUser.getRole() == Role.CUSTOMER && !Objects.equals(booking.getUser().getId(), currentUser.getId())) {
            throw new SecurityException("Unauthorized to reschedule this booking");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Cannot reschedule a cancelled booking");
        }
        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalStateException("Cannot reschedule a completed booking");
        }

        booking.setCheckIn(request.getNewDate());
        booking.setBookingDate(request.getNewDate());
        if (request.getNewTime() != null) booking.setBookingTime(request.getNewTime());
        booking.setStatus(BookingStatus.RESCHEDULED);
        if (request.getReason() != null) {
            booking.setNotes((booking.getNotes() != null ? booking.getNotes() + " | " : "") + "Rescheduled: " + request.getReason());
        }

        return bookingRepository.save(booking);
    }

    // ==========================================
    // CANCEL & REFUND (Rule 4)
    // ==========================================
    @Transactional
    public Booking cancelBooking(Long bookingId, CancelRequest request, User currentUser) {
        Booking booking = getBookingById(bookingId);

        if (currentUser.getRole() == Role.CUSTOMER && !Objects.equals(booking.getUser().getId(), currentUser.getId())) {
            throw new SecurityException("Unauthorized to cancel this booking");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }
        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel an already completed booking");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancellationReason(request.getReason() != null ? request.getReason() : "Customer cancelled");

        // 1. Calculate Refund (Rule 4)
        BigDecimal amountPaid = booking.getAmountPaid() != null ? booking.getAmountPaid() : BigDecimal.ZERO;
        BigDecimal refundAmount = BigDecimal.ZERO;

        if (amountPaid.compareTo(BigDecimal.ZERO) > 0) {
            boolean isFreeCancel = false;
            LocalDate cIn = booking.getCheckIn() != null ? booking.getCheckIn() : booking.getBookingDate();
            if (cIn != null) {
                long hoursUntilCheckIn = ChronoUnit.HOURS.between(
                        LocalDateTime.now(),
                        cIn.atTime(14, 0)
                );
                if (hoursUntilCheckIn >= 48) {
                    isFreeCancel = true;
                }
            }

            if (isFreeCancel) {
                // Free cancellation: 100% refund of amount paid
                refundAmount = amountPaid;
            } else {
                // Within 48 hours: forfeit deposit
                BigDecimal deposit = booking.getDepositRequired() != null ? booking.getDepositRequired() : BigDecimal.ZERO;
                if (amountPaid.compareTo(deposit) > 0) {
                    refundAmount = amountPaid.subtract(deposit);
                }
            }

            if (refundAmount.compareTo(BigDecimal.ZERO) > 0) {
                Payment refundPayment = new Payment();
                refundPayment.setBooking(booking);
                refundPayment.setTransactionRef("REF-" + System.currentTimeMillis());
                refundPayment.setMethod(PaymentMethod.KHQR);
                refundPayment.setPaymentType(PaymentType.REFUND);
                refundPayment.setAmountUsd(refundAmount.negate());
                refundPayment.setAmountKhr(refundAmount.multiply(BigDecimal.valueOf(4100)).negate());
                refundPayment.setStatus(PaymentStatus.REFUNDED);
                refundPayment.setPaidAt(LocalDateTime.now());
                paymentRepository.save(refundPayment);
                booking.getPayments().add(refundPayment);

                booking.setAmountPaid(amountPaid.subtract(refundAmount));
                booking.setPaymentStatus("REFUNDED");
            }
        }

        // 2. Restore room inventory back to stock (Rule 4 & inventory logic)
        if (booking.getRoom() != null && booking.getNumberOfRooms() != null && booking.getNumberOfRooms() > 0) {
            Room bookedRoom = booking.getRoom();
            int unitsToRestore = booking.getNumberOfRooms();

            int currentAvail = bookedRoom.getAvailableUnits() != null ? bookedRoom.getAvailableUnits() : 0;
            int totalR = bookedRoom.getTotalRooms() != null ? bookedRoom.getTotalRooms() : (currentAvail + unitsToRestore);
            int canRestoreToBooked = Math.max(0, totalR - currentAvail);
            int restoreFirst = Math.min(unitsToRestore, canRestoreToBooked);

            if (restoreFirst > 0) {
                roomRepository.restoreRoomInventory(bookedRoom.getId(), restoreFirst);
                unitsToRestore -= restoreFirst;
            }

            if (unitsToRestore > 0 && bookedRoom.getHotel() != null) {
                List<Room> sameTierRooms = roomRepository.findByHotelId(bookedRoom.getHotel().getId()).stream()
                        .filter(r -> !r.getId().equals(bookedRoom.getId()) && r.getType() == bookedRoom.getType() && Objects.equals(r.getRoomTier(), bookedRoom.getRoomTier()))
                        .collect(Collectors.toList());
                for (Room other : sameTierRooms) {
                    int oAvail = other.getAvailableUnits() != null ? other.getAvailableUnits() : 0;
                    int oTotal = other.getTotalRooms() != null ? other.getTotalRooms() : (oAvail + unitsToRestore);
                    int space = Math.max(0, oTotal - oAvail);
                    if (space > 0) {
                        int rAmt = Math.min(unitsToRestore, space);
                        roomRepository.restoreRoomInventory(other.getId(), rAmt);
                        unitsToRestore -= rAmt;
                        if (unitsToRestore <= 0) break;
                    }
                }
                if (unitsToRestore > 0) {
                    roomRepository.restoreRoomInventory(bookedRoom.getId(), unitsToRestore);
                }
            }
        }

        Booking updated = bookingRepository.save(booking);

        // Notification
        Notification notification = new Notification();
        notification.setUser(booking.getUser());
        notification.setTitle("Booking Cancelled #" + booking.getBookingCode());
        notification.setMessage("Your booking has been cancelled." + (refundAmount.compareTo(BigDecimal.ZERO) > 0 ? " Refund of US$" + refundAmount + " has been processed." : ""));
        notification.setType("BOOKING_CANCELLED");
        notificationRepository.save(notification);

        return updated;
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + id));
    }

    public Booking getBookingByCode(String code) {
        return bookingRepository.findByBookingCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with code: " + code));
    }

    public List<Booking> getMyBookings(Long userId) {
        return bookingRepository.findByUserIdOrderByBookingDateDescBookingTimeDesc(userId);
    }

    public List<Booking> getMyBookings(Long userId, String email) {
        if (userId != null) return getMyBookings(userId);
        if (email != null && !email.isBlank()) {
            return bookingRepository.findAll().stream()
                    .filter(b -> b.getUser() != null && email.equalsIgnoreCase(b.getUser().getEmail()))
                    .sorted((a, b) -> b.getBookingDate().compareTo(a.getBookingDate()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public List<Booking> getUpcomingBookings(Long userId) {
        return bookingRepository.findUpcomingBookings(userId, LocalDate.now());
    }

    public List<Booking> getUpcomingBookings(Long userId, String email) {
        if (userId != null) return getUpcomingBookings(userId);
        if (email != null && !email.isBlank()) {
            return bookingRepository.findAll().stream()
                    .filter(b -> b.getUser() != null && email.equalsIgnoreCase(b.getUser().getEmail()))
                    .filter(b -> b.getBookingDate() != null && b.getBookingDate().isAfter(LocalDate.now().minusDays(1)))
                    .sorted((a, b) -> a.getBookingDate().compareTo(b.getBookingDate()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public List<Booking> getHistoryBookings(Long userId) {
        return bookingRepository.findHistoryBookings(userId, LocalDate.now());
    }

    public List<Booking> getHistoryBookings(Long userId, String email) {
        if (userId != null) return getHistoryBookings(userId);
        if (email != null && !email.isBlank()) {
            return bookingRepository.findAll().stream()
                    .filter(b -> b.getUser() != null && email.equalsIgnoreCase(b.getUser().getEmail()))
                    .filter(b -> b.getBookingDate() != null && b.getBookingDate().isBefore(LocalDate.now()))
                    .sorted((a, b) -> b.getBookingDate().compareTo(a.getBookingDate()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public List<Booking> getHotelBookings(Long hotelId) {
        return bookingRepository.findByHotelIdOrderByCreatedAtDesc(hotelId);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Transactional
    public Booking updateBookingStatus(Long id, BookingStatus status) {
        Booking booking = getBookingById(id);
        booking.setStatus(status);

        // Synchronize Room lifecycle
        if (booking.getRoom() != null) {
            Room r = booking.getRoom();
            if (status == BookingStatus.CHECKED_IN) {
                r.setStatus(RoomStatus.BOOKED);
                r.setAvailableUnits(0);
                roomRepository.save(r);
            } else if (status == BookingStatus.COMPLETED) { // Check-out!
                r.setStatus(RoomStatus.CLEANING);
                r.setAvailableUnits(0);
                roomRepository.save(r);
            } else if (status == BookingStatus.CANCELLED) {
                int units = booking.getNumberOfRooms() != null && booking.getNumberOfRooms() > 0 ? booking.getNumberOfRooms() : 1;
                roomRepository.restoreRoomInventory(r.getId(), units);
                r.setStatus(RoomStatus.AVAILABLE);
                r.setAvailableUnits(r.getTotalRooms() != null && r.getTotalRooms() > 0 ? r.getTotalRooms() : 1);
                roomRepository.save(r);
            }
        }

        if (booking.getItems() != null) {
            for (BookingItem item : booking.getItems()) {
                if (item.getRoom() != null) {
                    Room r = item.getRoom();
                    if (status == BookingStatus.CHECKED_IN) {
                        r.setStatus(RoomStatus.BOOKED);
                        r.setAvailableUnits(0);
                        roomRepository.save(r);
                    } else if (status == BookingStatus.COMPLETED) {
                        r.setStatus(RoomStatus.CLEANING);
                        r.setAvailableUnits(0);
                        roomRepository.save(r);
                    } else if (status == BookingStatus.CANCELLED) {
                        int units = item.getQuantity() != null && item.getQuantity() > 0 ? item.getQuantity() : 1;
                        roomRepository.restoreRoomInventory(r.getId(), units);
                        r.setStatus(RoomStatus.AVAILABLE);
                        r.setAvailableUnits(r.getTotalRooms() != null && r.getTotalRooms() > 0 ? r.getTotalRooms() : 1);
                        roomRepository.save(r);
                    }
                }
            }
        }

        // Trigger review invitation notification ONLY when Check-out is completed by Admin
        User targetUser = booking.getUser();
        if (targetUser == null && booking.getGuestEmail() != null && !booking.getGuestEmail().isBlank()) {
            targetUser = userRepository.findByEmail(booking.getGuestEmail().trim()).orElse(null);
        }
        if (targetUser != null && status == BookingStatus.COMPLETED) {
            try {
                Notification reviewNotif = new Notification();
                reviewNotif.setUser(targetUser);
                reviewNotif.setTitle("Check-Out Complete! Rate Your Stay");
                String rName = booking.getRoom() != null ? booking.getRoom().getName() : (booking.getService() != null ? booking.getService().getName() : "Room");
                reviewNotif.setMessage("[CHECKOUT_BOOKING:" + booking.getId() + "] Check-out completed by hotel reception. Thank you for staying in " + rName + " (#" + booking.getBookingCode() + "). Tap to rate your experience!");
                reviewNotif.setType("REVIEW_INVITATION");
                notificationRepository.save(reviewNotif);
            } catch (Exception ignored) {}
        }

        return bookingRepository.save(booking);
    }

    /**
     * Automatic cleanup: Any unpaid pending checkout older than 10 minutes is automatically cancelled and its room released.
     */
    @org.springframework.scheduling.annotation.Scheduled(fixedRate = 30000)
    @Transactional
    public void cleanupExpiredPendingBookings() {
        LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(10);
        List<Booking> expired = bookingRepository.findAll().stream()
                .filter(b -> b.getStatus() == BookingStatus.PENDING
                        && "UNPAID".equalsIgnoreCase(b.getPaymentStatus())
                        && b.getCreatedAt() != null
                        && b.getCreatedAt().isBefore(tenMinutesAgo))
                .collect(Collectors.toList());

        for (Booking b : expired) {
            b.setStatus(BookingStatus.CANCELLED);
            b.setCancellationReason("Payment session expired (5 minutes timeout)");
            bookingRepository.save(b);

            if (b.getRoom() != null) {
                int units = b.getNumberOfRooms() != null && b.getNumberOfRooms() > 0 ? b.getNumberOfRooms() : 1;
                roomRepository.restoreRoomInventory(b.getRoom().getId(), units);
                Room r = b.getRoom();
                r.setStatus(RoomStatus.AVAILABLE);
                r.setAvailableUnits(r.getTotalRooms() != null && r.getTotalRooms() > 0 ? r.getTotalRooms() : 1);
                roomRepository.save(r);
            }
        }
    }

    @Transactional
    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking == null) return;

        // Restore room inventory if applicable
        if (booking.getRoom() != null) {
            int units = booking.getNumberOfRooms() != null && booking.getNumberOfRooms() > 0 ? booking.getNumberOfRooms() : 1;
            roomRepository.restoreRoomInventory(booking.getRoom().getId(), units);
            Room r = booking.getRoom();
            r.setStatus(RoomStatus.AVAILABLE);
            if (r.getAvailableUnits() == null || r.getAvailableUnits() <= 0) {
                r.setAvailableUnits(units);
            }
            roomRepository.save(r);
        }

        bookingRepository.delete(booking);
    }
}
