package com._3d.marketplace.services;

import com._3d.marketplace.entity.Role;
import com._3d.marketplace.entity.User;
import java.util.List;

public interface UserService {
    User findByEmail(String email);
    User findById(Long id);
    void assignRole(Long userId, Role role);
}
