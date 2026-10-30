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
    public String uploadAvatar(Long userId, org.springframework.web.multipart.MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file cannot be empty");
        }

        try {
            java.nio.file.Path uploadPath = java.nio.file.Paths.get("uploads", "avatars");
            if (!java.nio.file.Files.exists(uploadPath)) {
                java.nio.file.Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = "jpg";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            }

            String filename = "avatar_" + userId + "_" + System.currentTimeMillis() + "." + extension;
            java.nio.file.Path targetLocation = uploadPath.resolve(filename);
            java.nio.file.Files.copy(file.getInputStream(), targetLocation, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            String avatarUrl = "/uploads/avatars/" + filename;
            User user = getProfile(userId);
            user.setAvatarUrl(avatarUrl);
            userRepository.save(user);

            ActivityLog log = new ActivityLog(user.getId(), "AVATAR_UPLOADED", "User uploaded a new profile photo: " + filename);
            activityLogRepository.save(log);

            return avatarUrl;
        } catch (java.io.IOException ex) {
            throw new RuntimeException("Could not store file. Please try again!", ex);
        }
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = getProfile(userId);
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword()) && !request.getOldPassword().equals(user.getPassword())) {
            throw new IllegalArgumentException("Current password does not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.saveAndFlush(user);

        ActivityLog log = new ActivityLog(user.getId(), "PASSWORD_CHANGED", "User successfully changed their account password");
        activityLogRepository.save(log);
    }

    public List<ActivityLog> getUserActivity(Long userId) {
        return activityLogRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
