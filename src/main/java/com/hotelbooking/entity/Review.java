package com.hotelbooking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(length = 60)
    private String stayType = "Couple";

    @Column(length = 120)
    private String location = "Cambodia";

    @Column(length = 120)
    private String guestName;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Review() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getCustomer() { return customer; }
    public void setCustomer(User customer) { this.customer = customer; }

    public Hotel getHotel() { return hotel; }
    public void setHotel(Hotel hotel) { this.hotel = hotel; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getStayType() { return stayType != null ? stayType : "Couple"; }
    public void setStayType(String stayType) { this.stayType = stayType; }

    public String getLocation() { return location != null ? location : "Cambodia"; }
    public void setLocation(String location) { this.location = location; }

    public String getGuestName() {
        if (guestName != null && !guestName.isBlank()) return guestName;
        return customer != null && customer.getFullName() != null ? customer.getFullName() : "Verified Guest";
    }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
