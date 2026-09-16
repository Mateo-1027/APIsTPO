package com._3d.marketplace.services;

import com._3d.marketplace.controllers.auth.AuthenticationResponse;
import com._3d.marketplace.entity.Role;
import com._3d.marketplace.entity.User;
import com._3d.marketplace.entity.dto.AccountDeactivationRequest;
import com._3d.marketplace.entity.dto.EmailChangeRequest;
import com._3d.marketplace.entity.dto.PasswordChangeRequest;
import com._3d.marketplace.entity.dto.UserResponse;
import com._3d.marketplace.entity.dto.UserUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {
    UserResponse getProfile(User user);
    Page<UserResponse> getUsers(Pageable pageable);
    Optional<UserResponse> getUserById(Long id);
    Optional<UserResponse> getUserByEmail(String email);
    UserResponse updateProfile(User user, UserUpdateRequest request);
    AuthenticationResponse changeEmail(User user, EmailChangeRequest request);
    void changePassword(User user, PasswordChangeRequest request);
    void deactivateOwnAccount(User user, AccountDeactivationRequest request);
    void deactivateUser(Long userId);
    void activateUser(Long userId);
    void assignRole(Long userId, Role role);
    void removeRole(Long userId, Role role);
}
