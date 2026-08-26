package com._3d.marketplace.services;

import com._3d.marketplace.entity.Role;
import com._3d.marketplace.entity.User;
import com._3d.marketplace.exceptions.UserNotFoundException;
import com._3d.marketplace.repositories.RoleRepository;
import com._3d.marketplace.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("No se encontró el usuario con el username: " + username));
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("No se encontró el usuario con el id: " + id));
    }

    @Override
    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void assignRole(Long userId, Long roleId) {
        User user = findById(userId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("El rol no existe"));
        user.getRoles().add(role);
        userRepository.save(user);
    }
}
