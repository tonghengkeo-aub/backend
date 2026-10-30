package com.hotelbooking.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(nullable = false, length = 30)
    private String roomNumber;

    @Column(length = 120)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private RoomType type = RoomType.DOUBLE;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal priceUsd;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal priceKhr;

    @Column(nullable = false)
    private Integer maxGuests = 2;

    @Column(length = 60)
    private String bedType = "1 King Bed";

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String images;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoomStatus status = RoomStatus.AVAILABLE;

    @Column
    private Double rating = 4.8;

    @Column
    private Integer starRating = 5;

    @Column
    private Integer reviewCount = 128;

    @Column(length = 120)
    private String foodType = "Room only";

    @Column(length = 500)
    private String mapUrl = "https://maps.app.goo.gl/HL3kPor7pDvYhUBT8";

    @Column(precision = 10, scale = 2)
    private BigDecimal deluxePriceExtra = BigDecimal.valueOf(30.00);

    @Column(precision = 10, scale = 2)
    private BigDecimal premiumPriceExtra = BigDecimal.valueOf(50.00);

    @Column(length = 30)
    private String roomTier = "STANDARD";

    @Column(length = 30)
    private String roomSize = "28 m²";

    @Column(columnDefinition = "TEXT")
    private String amenities = "Free WiFi, Tea/Coffee maker, Balcony, Attached bathroom, City view";

    @Column
    private Integer availableUnits = 1;

    @Column
    private Integer totalRooms = 1;

    @Column
    private Boolean freeCancellation = true;

    @Column
    private Boolean noPrepayment = true;

    @Column
    private Boolean includesBreakfast = false;

    @Column
    private Boolean includesMassage = false;

    @Column(columnDefinition = "TEXT")
    private String standardInfo = "Standard comfort, Queen bed, basic cooling (Fan / basic AC), Free WiFi.";

    @Column(columnDefinition = "TEXT")
    private String deluxeInfo = "King Bed, Air Conditioning (AC), Flat-screen TV, Balcony.";

    @Column(columnDefinition = "TEXT")
    private String premiumInfo = "King Bed, AC, Breakfast included, Flat-screen TV, 15-min massage.";

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Room() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.type == null) this.type = RoomType.DOUBLE;
        if (this.status == null) this.status = RoomStatus.AVAILABLE;
        if (this.maxGuests == null) this.maxGuests = 2;
        if (this.priceKhr == null && this.priceUsd != null) {
            this.priceKhr = this.priceUsd.multiply(BigDecimal.valueOf(4100));
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        if (this.priceKhr == null && this.priceUsd != null) {
            this.priceKhr = this.priceUsd.multiply(BigDecimal.valueOf(4100));
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Hotel getHotel() { return hotel; }
    public void setHotel(Hotel hotel) { this.hotel = hotel; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public RoomType getType() { return type; }
    public void setType(RoomType type) { this.type = type; }

    public BigDecimal getPriceUsd() { return priceUsd; }
    public void setPriceUsd(BigDecimal priceUsd) { this.priceUsd = priceUsd; }

    public BigDecimal getPriceKhr() { return priceKhr; }
    public void setPriceKhr(BigDecimal priceKhr) { this.priceKhr = priceKhr; }

    public Integer getMaxGuests() { return maxGuests; }
    public void setMaxGuests(Integer maxGuests) { this.maxGuests = maxGuests; }

    public String getBedType() { return bedType; }
    public void setBedType(String bedType) { this.bedType = bedType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }

    public RoomStatus getStatus() { return status; }
    public void setStatus(RoomStatus status) { this.status = status; }

    public Double getRating() { return rating != null ? rating : 4.8; }
    public void setRating(Double rating) { this.rating = rating; }

    public Integer getStarRating() { return starRating != null ? starRating : 5; }
    public void setStarRating(Integer starRating) { this.starRating = starRating; }

    public Integer getReviewCount() { return reviewCount != null ? reviewCount : 128; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }

    public String getFoodType() { return foodType != null ? foodType : "Breakfast included"; }
    public void setFoodType(String foodType) { this.foodType = foodType; }

    public String getMapUrl() { return mapUrl; }
    public void setMapUrl(String mapUrl) { this.mapUrl = mapUrl; }

    public BigDecimal getDeluxePriceExtra() {
        return deluxePriceExtra != null ? deluxePriceExtra : BigDecimal.valueOf(30.00);
    }
    public void setDeluxePriceExtra(BigDecimal deluxePriceExtra) {
        this.deluxePriceExtra = deluxePriceExtra;
    }

    public BigDecimal getPremiumPriceExtra() {
        return premiumPriceExtra != null ? premiumPriceExtra : BigDecimal.valueOf(50.00);
    }
    public void setPremiumPriceExtra(BigDecimal premiumPriceExtra) {
        this.premiumPriceExtra = premiumPriceExtra;
    }

    public String getRoomTier() { return roomTier != null ? roomTier : "STANDARD"; }
    public void setRoomTier(String roomTier) { this.roomTier = roomTier; }

    public String getRoomSize() { return roomSize != null ? roomSize : "28 m²"; }
    public void setRoomSize(String roomSize) { this.roomSize = roomSize; }

    public String getAmenities() { return amenities != null ? amenities : "Free WiFi, Tea/Coffee maker, Balcony, Attached bathroom, City view"; }
    public void setAmenities(String amenities) { this.amenities = amenities; }

    public Integer getAvailableUnits() { return availableUnits != null ? availableUnits : 3; }
    public void setAvailableUnits(Integer availableUnits) { this.availableUnits = availableUnits; }

    public Boolean getFreeCancellation() { return freeCancellation != null ? freeCancellation : true; }
    public void setFreeCancellation(Boolean freeCancellation) { this.freeCancellation = freeCancellation; }

    public Boolean getNoPrepayment() { return noPrepayment != null ? noPrepayment : true; }
    public void setNoPrepayment(Boolean noPrepayment) { this.noPrepayment = noPrepayment; }

    public Boolean getIncludesBreakfast() { return includesBreakfast != null ? includesBreakfast : false; }
    public void setIncludesBreakfast(Boolean includesBreakfast) { this.includesBreakfast = includesBreakfast; }

    public Boolean getIncludesMassage() { return includesMassage != null ? includesMassage : false; }
    public void setIncludesMassage(Boolean includesMassage) { this.includesMassage = includesMassage; }

    public String getStandardInfo() {
        return standardInfo != null ? standardInfo : "Standard comfort, Queen bed, basic cooling (Fan / basic AC), Free WiFi.";
    }
    public void setStandardInfo(String standardInfo) {
        this.standardInfo = standardInfo;
    }

    public String getDeluxeInfo() {
        return deluxeInfo != null ? deluxeInfo : "King Bed, Air Conditioning (AC), Flat-screen TV, Balcony.";
    }
    public void setDeluxeInfo(String deluxeInfo) {
        this.deluxeInfo = deluxeInfo;
    }

    public String getPremiumInfo() {
        return premiumInfo != null ? premiumInfo : "King Bed, AC, Breakfast included, Flat-screen TV, 15-min massage.";
    }
    public void setPremiumInfo(String premiumInfo) {
        this.premiumInfo = premiumInfo;
    }

    public Integer getTotalRooms() {
        return totalRooms != null ? totalRooms : (availableUnits != null ? availableUnits : 3);
    }
    public void setTotalRooms(Integer totalRooms) {
        this.totalRooms = totalRooms;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
