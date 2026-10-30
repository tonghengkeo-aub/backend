package com.hotelbooking.dto;

public class ReviewRequest {
    private Long hotelId;
    private Long customerId;
    private Integer rating;
    private String comment;
    private String stayType;
    private String location;
    private String guestName;

    public ReviewRequest() {}

    public Long getHotelId() { return hotelId; }
    public void setHotelId(Long hotelId) { this.hotelId = hotelId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getStayType() { return stayType; }
    public void setStayType(String stayType) { this.stayType = stayType; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
}
