package com.hotelbooking.dto;

import com.hotelbooking.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @NotBlank
    private String fullName;

    private String phone;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6)
    private String password;

    private Role role = Role.CUSTOMER;

    // Optional fields if registering as HOTEL_OWNER
    private String hotelName;
    private String hotelProvince;
    private String hotelAddress;
    private String hotelPhone;
    private String hotelDescription;

    public RegisterRequest() {}

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }

    public String getHotelProvince() { return hotelProvince; }
    public void setHotelProvince(String hotelProvince) { this.hotelProvince = hotelProvince; }

    public String getHotelAddress() { return hotelAddress; }
    public void setHotelAddress(String hotelAddress) { this.hotelAddress = hotelAddress; }

    public String getHotelPhone() { return hotelPhone; }
    public void setHotelPhone(String hotelPhone) { this.hotelPhone = hotelPhone; }

    public String getHotelDescription() { return hotelDescription; }
    public void setHotelDescription(String hotelDescription) { this.hotelDescription = hotelDescription; }
}
