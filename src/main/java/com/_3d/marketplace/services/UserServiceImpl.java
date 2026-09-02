package com._3d.marketplace.services;

import com._3d.marketplace.entity.Role;
import com._3d.marketplace.entity.User;
import com._3d.marketplace.exceptions.UserNotFoundException;
import com._3d.marketplace.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("No se encontró el usuario con el email: " + email));
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("No se encontró el usuario con el id: " + id));
    }

    @Override
    @Transactional
    public void assignRole(Long userId, Role role) {
        User user = findById(userId);
        user.getRoles().add(role);
        userRepository.save(user);
    }
}
