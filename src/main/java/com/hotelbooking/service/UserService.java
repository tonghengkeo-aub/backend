package com.hotelbooking.service;

import com.hotelbooking.dto.ChangePasswordRequest;
import com.hotelbooking.dto.UpdateProfileRequest;
import com.hotelbooking.entity.ActivityLog;
import com.hotelbooking.entity.User;
import com.hotelbooking.repository.ActivityLogRepository;
import com.hotelbooking.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ActivityLogRepository activityLogRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       ActivityLogRepository activityLogRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.activityLogRepository = activityLogRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getProfile(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
    }

    @Transactional
    public User updateProfile(Long userId, UpdateProfileRequest request) {
        User user = getProfile(userId);
        user.setFullName(request.getFullName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getGender() != null) user.setGender(request.getGender());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());

        User updated = userRepository.save(user);

        ActivityLog log = new ActivityLog(user.getId(), "PROFILE_UPDATED", "User updated their profile information");
        activityLogRepository.save(log);

        return updated;
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = getProfile(userId);
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword()) && !request.getOldPassword().equals(user.getPassword())) {
            throw new IllegalArgumentException("Current password does not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        ActivityLog log = new ActivityLog(user.getId(), "PASSWORD_CHANGED", "User successfully changed their account password");
        activityLogRepository.save(log);
    }

    public List<ActivityLog> getUserActivity(Long userId) {
        return activityLogRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
