package com.hotelbooking.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PriceCalculationRequest {

    private Long roomId;
    private Integer quantity = 1;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private String couponCode;
    private List<ExtraItemDto> extras = new ArrayList<>();

    public static class ExtraItemDto {
        private String serviceName;
        private BigDecimal price;
        private Integer quantity = 1;

        public ExtraItemDto() {}

        public ExtraItemDto(String serviceName, BigDecimal price, Integer quantity) {
            this.serviceName = serviceName;
            this.price = price;
            this.quantity = quantity != null ? quantity : 1;
        }

        public String getServiceName() { return serviceName; }
        public void setServiceName(String serviceName) { this.serviceName = serviceName; }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }

        public Integer getQuantity() { return quantity != null ? quantity : 1; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    public PriceCalculationRequest() {}

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public Integer getQuantity() { return quantity != null && quantity > 0 ? quantity : 1; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public LocalDate getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDate checkIn) { this.checkIn = checkIn; }

    public LocalDate getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDate checkOut) { this.checkOut = checkOut; }

    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }

    public List<ExtraItemDto> getExtras() { return extras != null ? extras : new ArrayList<>(); }
    public void setExtras(List<ExtraItemDto> extras) { this.extras = extras; }
}
