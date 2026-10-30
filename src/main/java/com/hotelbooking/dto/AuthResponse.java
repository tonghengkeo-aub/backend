package com.hotelbooking.dto;

import com.hotelbooking.entity.Role;

public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private Role role;
    private Long hotelId; // Populated if role is HOTEL_OWNER
    private Boolean isApproved = true;
    private String avatarUrl;
    private String gender;
    private String address;

    public AuthResponse() {}

    public AuthResponse(String token, Long id, String fullName, String email, String phone, Role role, Long hotelId, Boolean isApproved) {
        this.token = token;
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.hotelId = hotelId;
        this.isApproved = isApproved != null ? isApproved : true;
    }

    public AuthResponse(String token, Long id, String fullName, String email, String phone, Role role, Long hotelId, Boolean isApproved, String avatarUrl, String gender, String address) {
        this.token = token;
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.hotelId = hotelId;
        this.isApproved = isApproved != null ? isApproved : true;
        this.avatarUrl = avatarUrl;
        this.gender = gender;
        this.address = address;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Long getHotelId() { return hotelId; }
    public void setHotelId(Long hotelId) { this.hotelId = hotelId; }

    public Boolean getIsApproved() { return isApproved; }
    public void setIsApproved(Boolean isApproved) { this.isApproved = isApproved; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
