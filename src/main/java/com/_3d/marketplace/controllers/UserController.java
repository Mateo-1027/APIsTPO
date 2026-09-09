package com._3d.marketplace.controllers;

import com._3d.marketplace.entity.Role;
import com._3d.marketplace.entity.User;
import com._3d.marketplace.entity.dto.UserResponse;
import com._3d.marketplace.exceptions.UserNotFoundException;
import com._3d.marketplace.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(userService.getProfile(user));
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

    @PostMapping("/{userId}/roles/{role}")
    public ResponseEntity<Void> assignRole(@PathVariable Long userId, @PathVariable Role role) {
        userService.assignRole(userId, role);
        return ResponseEntity.ok().build();
    }
}
