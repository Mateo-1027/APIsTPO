package com._3d.marketplace.services;

import com._3d.marketplace.entity.Role;
import com._3d.marketplace.entity.User;
import com._3d.marketplace.entity.dto.UserResponse;

import java.util.Optional;

public interface UserService {
    UserResponse getProfile(User user);
    Optional<UserResponse> getUserById(Long id);
    Optional<UserResponse> getUserByEmail(String email);
    void assignRole(Long userId, Role role);
}
