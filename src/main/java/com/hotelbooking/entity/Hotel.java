package com.hotelbooking.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "hotels")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false, length = 180)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false, length = 80)
    private String province; // e.g. Phnom Penh, Siem Reap, Preah Sihanouk, Kampot

    @Column(length = 40)
    private String phone;

    @Column(length = 120)
    private String email;

    @Column(precision = 2, scale = 1)
    private BigDecimal rating = BigDecimal.valueOf(4.5);

    @Column(precision = 4, scale = 2)
    private BigDecimal commissionRate = BigDecimal.valueOf(12.00);

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private HotelStatus status = HotelStatus.PENDING;

    @Column(length = 500)
    private String mainImage;

    @Column(length = 500)
    private String facilities; // comma-separated or JSON string

    @Column(length = 500)
    private String mapUrl = "https://maps.google.com/?q=Riverside+Blvd+Phnom+Penh";

    @Column(length = 255)
    private String tagline = "Luxury 5-Star Boutique & Riverside Retreat";

    @Column(name = "star_rating")
    private Integer starRating = 5;

    @Column(length = 50)
    private String checkInTime = "02:00 PM";

    @Column(length = 50)
    private String checkOutTime = "12:00 PM";

    @Column(columnDefinition = "TEXT")
    private String policy = "Free cancellation up to 24 hours prior to arrival. Valid Passport or National ID required at check-in.";

    @Column(length = 255)
    private String website = "https://bosshenghotel.com";

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Hotel() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.rating == null) this.rating = BigDecimal.valueOf(4.5);
        if (this.commissionRate == null) this.commissionRate = BigDecimal.valueOf(12.00);
        if (this.status == null) this.status = HotelStatus.PENDING;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }

    public BigDecimal getCommissionRate() { return commissionRate; }
    public void setCommissionRate(BigDecimal commissionRate) { this.commissionRate = commissionRate; }

    public HotelStatus getStatus() { return status; }
    public void setStatus(HotelStatus status) { this.status = status; }

    public String getMainImage() { return mainImage; }
    public void setMainImage(String mainImage) { this.mainImage = mainImage; }

    public String getFacilities() { return facilities; }
    public void setFacilities(String facilities) { this.facilities = facilities; }

    public String getMapUrl() { return mapUrl != null ? mapUrl : "https://maps.google.com/?q=Riverside+Blvd+Phnom+Penh"; }
    public void setMapUrl(String mapUrl) { this.mapUrl = mapUrl; }

    public String getTagline() { return tagline != null ? tagline : "Luxury 5-Star Boutique & Riverside Retreat"; }
    public void setTagline(String tagline) { this.tagline = tagline; }

    public Integer getStarRating() { return starRating != null ? starRating : 5; }
    public void setStarRating(Integer starRating) { this.starRating = starRating; }

    public String getCheckInTime() { return checkInTime != null ? checkInTime : "02:00 PM"; }
    public void setCheckInTime(String checkInTime) { this.checkInTime = checkInTime; }

    public String getCheckOutTime() { return checkOutTime != null ? checkOutTime : "12:00 PM"; }
    public void setCheckOutTime(String checkOutTime) { this.checkOutTime = checkOutTime; }

    public String getPolicy() { return policy != null ? policy : "Free cancellation up to 24 hours prior to arrival. Valid Passport or National ID required at check-in."; }
    public void setPolicy(String policy) { this.policy = policy; }

    public String getWebsite() { return website != null ? website : "https://bosshenghotel.com"; }
    public void setWebsite(String website) { this.website = website; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
