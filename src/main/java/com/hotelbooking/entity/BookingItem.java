package com.hotelbooking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "booking_items")
public class BookingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    @JsonIgnore
    private Booking booking;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(nullable = false, length = 120)
    private String roomName;

    @Column(length = 50)
    private String roomType;

    @Column(length = 50)
    private String roomTier;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal pricePerNight;

    @Column(nullable = false)
    private Integer nights = 1;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal lineTotal;

    public BookingItem() {}

    public BookingItem(Booking booking, Room room, String roomName, String roomType, String roomTier, Integer quantity, BigDecimal pricePerNight, Integer nights, BigDecimal lineTotal) {
        this.booking = booking;
        this.room = room;
        this.roomName = roomName;
        this.roomType = roomType;
        this.roomTier = roomTier;
        this.quantity = quantity != null ? quantity : 1;
        this.pricePerNight = pricePerNight;
        this.nights = nights != null ? nights : 1;
        this.lineTotal = lineTotal;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public String getRoomTier() { return roomTier; }
    public void setRoomTier(String roomTier) { this.roomTier = roomTier; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; }

    public Integer getNights() { return nights; }
    public void setNights(Integer nights) { this.nights = nights; }

    public BigDecimal getLineTotal() { return lineTotal; }
    public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }
}
