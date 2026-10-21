package com.hotelbooking.controller;

import com.hotelbooking.dto.ChangePasswordRequest;
import com.hotelbooking.dto.UpdateProfileRequest;
import com.hotelbooking.entity.ActivityLog;
import com.hotelbooking.entity.User;
import com.hotelbooking.security.CustomUserDetails;
import com.hotelbooking.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(userService.getProfile(userDetails.getId()));
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User updated = userService.updateProfile(userDetails.getId(), request);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.changePassword(userDetails.getId(), request);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

    @GetMapping("/activities")
    public ResponseEntity<List<ActivityLog>> getUserActivities(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(userService.getUserActivity(userDetails.getId()));
    }
}
