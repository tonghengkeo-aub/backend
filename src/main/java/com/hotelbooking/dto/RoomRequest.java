package com.hotelbooking.dto;

import com.hotelbooking.entity.RoomStatus;
import com.hotelbooking.entity.RoomType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class RoomRequest {
    private Long hotelId = 1L;

    @NotBlank
    private String roomNumber;

    private String name;

    @NotNull
    private RoomType type = RoomType.DOUBLE;

    @NotNull
    private BigDecimal priceUsd;

    private Integer maxGuests = 2;
    private String bedType = "1 King Bed";
    private String description;
    private String images;
    private RoomStatus status = RoomStatus.AVAILABLE;
    private Double rating = 4.8;
    private Integer starRating = 5;
    private Integer reviewCount = 128;
    private String foodType = "Breakfast included";
    private String mapUrl = "https://maps.google.com/?q=Riverside+Blvd+Phnom+Penh";

    private BigDecimal deluxePriceExtra = BigDecimal.valueOf(30.00);
    private BigDecimal premiumPriceExtra = BigDecimal.valueOf(50.00);

    private String standardInfo;
    private String deluxeInfo;
    private String premiumInfo;

    private String roomTier = "STANDARD";
    private String roomSize = "28 m²";
    private String amenities = "Free WiFi, Tea/Coffee maker, Balcony, Attached bathroom, City view";
    private Integer availableUnits = 3;
    private Integer totalRooms = 3;
    private Integer addQuantity;
    private Boolean freeCancellation = true;
    private Boolean noPrepayment = true;
    private Boolean includesBreakfast = false;
    private Boolean includesMassage = false;

    public RoomRequest() {}

    public Long getHotelId() { return hotelId; }
    public void setHotelId(Long hotelId) { this.hotelId = hotelId; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public RoomType getType() { return type; }
    public void setType(RoomType type) { this.type = type; }

    public BigDecimal getPriceUsd() { return priceUsd; }
    public void setPriceUsd(BigDecimal priceUsd) { this.priceUsd = priceUsd; }

    public BigDecimal getDeluxePriceExtra() { return deluxePriceExtra != null ? deluxePriceExtra : BigDecimal.valueOf(30.00); }
    public void setDeluxePriceExtra(BigDecimal deluxePriceExtra) { this.deluxePriceExtra = deluxePriceExtra; }

    public BigDecimal getPremiumPriceExtra() { return premiumPriceExtra != null ? premiumPriceExtra : BigDecimal.valueOf(50.00); }
    public void setPremiumPriceExtra(BigDecimal premiumPriceExtra) { this.premiumPriceExtra = premiumPriceExtra; }

    public String getStandardInfo() { return standardInfo; }
    public void setStandardInfo(String standardInfo) { this.standardInfo = standardInfo; }

    public String getDeluxeInfo() { return deluxeInfo; }
    public void setDeluxeInfo(String deluxeInfo) { this.deluxeInfo = deluxeInfo; }

    public String getPremiumInfo() { return premiumInfo; }
    public void setPremiumInfo(String premiumInfo) { this.premiumInfo = premiumInfo; }

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

    public String getRoomTier() { return roomTier != null ? roomTier : "STANDARD"; }
    public void setRoomTier(String roomTier) { this.roomTier = roomTier; }

    public String getRoomSize() { return roomSize != null ? roomSize : "28 m²"; }
    public void setRoomSize(String roomSize) { this.roomSize = roomSize; }

    public String getAmenities() { return amenities != null ? amenities : "Free WiFi, Tea/Coffee maker, Balcony, Attached bathroom, City view"; }
    public void setAmenities(String amenities) { this.amenities = amenities; }

    public Integer getAvailableUnits() { return availableUnits != null ? availableUnits : 3; }
    public void setAvailableUnits(Integer availableUnits) { this.availableUnits = availableUnits; }

    public Integer getTotalRooms() { return totalRooms != null ? totalRooms : (availableUnits != null ? availableUnits : 3); }
    public void setTotalRooms(Integer totalRooms) { this.totalRooms = totalRooms; }

    public Integer getAddQuantity() { return addQuantity; }
    public void setAddQuantity(Integer addQuantity) { this.addQuantity = addQuantity; }

    public Boolean getFreeCancellation() { return freeCancellation != null ? freeCancellation : true; }
    public void setFreeCancellation(Boolean freeCancellation) { this.freeCancellation = freeCancellation; }

    public Boolean getNoPrepayment() { return noPrepayment != null ? noPrepayment : true; }
    public void setNoPrepayment(Boolean noPrepayment) { this.noPrepayment = noPrepayment; }

    public Boolean getIncludesBreakfast() { return includesBreakfast != null ? includesBreakfast : false; }
    public void setIncludesBreakfast(Boolean includesBreakfast) { this.includesBreakfast = includesBreakfast; }

    public Boolean getIncludesMassage() { return includesMassage != null ? includesMassage : false; }
    public void setIncludesMassage(Boolean includesMassage) { this.includesMassage = includesMassage; }
}
