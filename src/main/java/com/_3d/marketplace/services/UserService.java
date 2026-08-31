package com._3d.marketplace.services;

import com._3d.marketplace.entity.Role;
import com._3d.marketplace.entity.User;
import java.util.List;

public interface UserService {
    User findByUsername(String username);
    User findById(Long id);
    User createUser(User user);
    void assignRole(Long userId, Role role);
}
