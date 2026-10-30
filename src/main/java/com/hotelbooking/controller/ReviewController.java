package com.hotelbooking.controller;

import com.hotelbooking.dto.ReviewRequest;
import com.hotelbooking.entity.Hotel;
import com.hotelbooking.entity.Notification;
import com.hotelbooking.entity.Review;
import com.hotelbooking.entity.User;
import com.hotelbooking.repository.HotelRepository;
import com.hotelbooking.repository.NotificationRepository;
import com.hotelbooking.repository.ReviewRepository;
import com.hotelbooking.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public ReviewController(ReviewRepository reviewRepository,
                            HotelRepository hotelRepository,
                            UserRepository userRepository,
                            NotificationRepository notificationRepository) {
        this.reviewRepository = reviewRepository;
        this.hotelRepository = hotelRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<Review>> getHotelReviews(@PathVariable Long hotelId) {
        List<Review> list = reviewRepository.findByHotelIdOrderByCreatedAtDesc(hotelId);
        return ResponseEntity.ok(list);
    }

    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        return ResponseEntity.ok(reviewRepository.findAllByOrderByCreatedAtDesc());
    }

    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody ReviewRequest req) {
        Review review = new Review();

        Hotel hotel = null;
        if (req.getHotelId() != null) {
            hotel = hotelRepository.findById(req.getHotelId()).orElse(null);
        }
        if (hotel == null) {
            hotel = hotelRepository.findAll().stream().findFirst().orElse(null);
        }
        review.setHotel(hotel);

        User customer = null;
        if (req.getCustomerId() != null) {
            customer = userRepository.findById(req.getCustomerId()).orElse(null);
        }
        if (customer == null) {
            customer = userRepository.findAll().stream()
                    .filter(u -> u.getRole() != null && "CUSTOMER".equalsIgnoreCase(u.getRole().name()))
                    .findFirst().orElse(null);
        }
        review.setCustomer(customer);

        review.setRating(req.getRating() != null && req.getRating() >= 1 ? req.getRating() : 5);
        review.setComment(req.getComment() != null ? req.getComment().trim() : "Wonderful stay! Highly recommended.");
        review.setStayType(req.getStayType() != null && !req.getStayType().isBlank() ? req.getStayType().trim() : "Couple");
        review.setLocation(req.getLocation() != null && !req.getLocation().isBlank() ? req.getLocation().trim() : "Cambodia");
        review.setGuestName(req.getGuestName() != null && !req.getGuestName().isBlank()
                ? req.getGuestName().trim()
                : (customer != null && customer.getFullName() != null ? customer.getFullName() : "Verified Guest"));

        Review saved = reviewRepository.save(review);

        // Recalculate and update hotel rating dynamically
        if (hotel != null) {
            List<Review> allReviews = reviewRepository.findAll();
            if (!allReviews.isEmpty()) {
                double avg = allReviews.stream()
                        .mapToInt(Review::getRating)
                        .average()
                        .orElse(5.0);
                hotel.setRating(BigDecimal.valueOf(Math.round(avg * 10.0) / 10.0));
                hotelRepository.save(hotel);
            }
        }

        // Send a Thank-You In-App Notification to Customer
        if (customer != null) {
            Notification notif = new Notification();
            notif.setUser(customer);
            notif.setTitle("Review Published ⭐⭐⭐⭐⭐");
            notif.setMessage("Thank you " + review.getGuestName() + "! Your review for " + (hotel != null ? hotel.getName() : "hotel") + " is now live.");
            notif.setType("REVIEW_SUBMITTED");
            notificationRepository.save(notif);
        }

        return ResponseEntity.ok(saved);
    }
}
