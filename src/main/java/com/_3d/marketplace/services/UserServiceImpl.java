package com._3d.marketplace.services;

import com._3d.marketplace.entity.Role;
import com._3d.marketplace.entity.User;
import com._3d.marketplace.entity.dto.UserResponse;
import com._3d.marketplace.exceptions.UserNotFoundException;
import com._3d.marketplace.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserResponse getProfile(User user) {
        return mapToResponse(user);
    }

    @Override
    public Optional<UserResponse> getUserById(Long id) {
        return userRepository.findById(id).map(this::mapToResponse);
    }

    @Override
    public Optional<UserResponse> getUserByEmail(String email) {
        return userRepository.findByEmail(email).map(this::mapToResponse);
    }

    @Override
    @Transactional
    public void assignRole(Long userId, Role role) {
        User user = findById(userId);
        user.getRoles().add(role);
        userRepository.save(user);
    }

    private User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("No se encontró el usuario con el id: " + id));
    }

    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setSurname(user.getSurname());
        response.setEmail(user.getEmail());
        response.setRoles(user.getRoles());
        return response;
    }
}
