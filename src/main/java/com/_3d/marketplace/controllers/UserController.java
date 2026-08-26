package com._3d.marketplace.controllers;

import com._3d.marketplace.entity.User;
import com._3d.marketplace.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }
    
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.findByUsername(username));
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PostMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<Void> assignRole(@PathVariable Long userId, @PathVariable Long roleId) {
        userService.assignRole(userId, roleId);
        return ResponseEntity.ok().build();
    }
}
