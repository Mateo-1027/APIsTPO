package com._3d.marketplace.controllers;

import com._3d.marketplace.controllers.auth.AuthenticationResponse;
import com._3d.marketplace.entity.Role;
import com._3d.marketplace.entity.User;
import com._3d.marketplace.entity.dto.AccountDeactivationRequest;
import com._3d.marketplace.entity.dto.EmailChangeRequest;
import com._3d.marketplace.entity.dto.PasswordChangeRequest;
import com._3d.marketplace.entity.dto.UserResponse;
import com._3d.marketplace.entity.dto.UserUpdateRequest;
import com._3d.marketplace.exceptions.UserNotFoundException;
import com._3d.marketplace.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserResponse>> getUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.getUsers(pageable));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(userService.getProfile(user));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUser(
            @AuthenticationPrincipal User user,
            @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateProfile(user, request));
    }

    @PatchMapping("/me/email")
    public ResponseEntity<AuthenticationResponse> changeEmail(
            @AuthenticationPrincipal User user,
            @RequestBody EmailChangeRequest request) {
        return ResponseEntity.ok(userService.changeEmail(user, request));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal User user,
            @RequestBody PasswordChangeRequest request) {
        userService.changePassword(user, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deactivateOwnAccount(
            @AuthenticationPrincipal User user,
            @RequestBody AccountDeactivationRequest request) {
        userService.deactivateOwnAccount(user, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id)
                .orElseThrow(() -> new UserNotFoundException("No se encontró el usuario con el id: " + id)));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("No se encontró el usuario con el email: " + email)));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long userId) {
        userService.deactivateUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/activate")
    public ResponseEntity<UserResponse> activateUser(@PathVariable Long userId) {
        userService.activateUser(userId);
        return ResponseEntity.ok(userService.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("No se encontró el usuario con el id: " + userId)));
    }

    @PostMapping("/{userId}/roles/{role}")
    public ResponseEntity<Void> assignRole(@PathVariable Long userId, @PathVariable Role role) {
        userService.assignRole(userId, role);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/roles/{role}")
    public ResponseEntity<Void> removeRole(@PathVariable Long userId, @PathVariable Role role) {
        userService.removeRole(userId, role);
        return ResponseEntity.noContent().build();
    }
}
