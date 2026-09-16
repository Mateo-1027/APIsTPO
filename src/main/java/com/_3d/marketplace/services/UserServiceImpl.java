package com._3d.marketplace.services;

import com._3d.marketplace.controllers.auth.AuthenticationResponse;
import com._3d.marketplace.controllers.config.JwtService;
import com._3d.marketplace.entity.Role;
import com._3d.marketplace.entity.User;
import com._3d.marketplace.entity.dto.AccountDeactivationRequest;
import com._3d.marketplace.entity.dto.EmailChangeRequest;
import com._3d.marketplace.entity.dto.PasswordChangeRequest;
import com._3d.marketplace.entity.dto.UserResponse;
import com._3d.marketplace.entity.dto.UserUpdateRequest;
import com._3d.marketplace.exceptions.EmailAlreadyUsedException;
import com._3d.marketplace.exceptions.InvalidPasswordException;
import com._3d.marketplace.exceptions.UserNotFoundException;
import com._3d.marketplace.repositories.ProductRepository;
import com._3d.marketplace.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Override
    public UserResponse getProfile(User user) {
        return mapToResponse(user);
    }

    @Override
    public Page<UserResponse> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::mapToResponse);
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
    public UserResponse updateProfile(User user, UserUpdateRequest request) {
        User current = findById(user.getId());
        if (request.getName() != null) {
            if (request.getName().isBlank()) {
                throw new IllegalArgumentException("El nombre no puede estar vacío");
            }
            current.setName(request.getName().trim());
        }
        if (request.getSurname() != null) {
            if (request.getSurname().isBlank()) {
                throw new IllegalArgumentException("El apellido no puede estar vacío");
            }
            current.setSurname(request.getSurname().trim());
        }
        return mapToResponse(userRepository.save(current));
    }

    @Override
    @Transactional
    public AuthenticationResponse changeEmail(User user, EmailChangeRequest request) {
        User current = findById(user.getId());
        verifyPassword(current, request.getCurrentPassword());

        String newEmail = request.getNewEmail() == null ? "" : request.getNewEmail().trim();
        if (newEmail.isEmpty() || !newEmail.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("El email no es válido");
        }
        if (newEmail.equalsIgnoreCase(current.getEmail())) {
            throw new IllegalArgumentException("El nuevo email debe ser distinto al actual");
        }
        if (userRepository.findByEmail(newEmail).isPresent()) {
            throw new EmailAlreadyUsedException("El email ya está en uso");
        }

        current.setEmail(newEmail);
        userRepository.save(current);
        return AuthenticationResponse.builder()
                .accessToken(jwtService.generateToken(current))
                .build();
    }

    @Override
    @Transactional
    public void changePassword(User user, PasswordChangeRequest request) {
        User current = findById(user.getId());
        verifyPassword(current, request.getCurrentPassword());

        String newPassword = request.getNewPassword();
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("La nueva contraseña no puede estar vacía");
        }
        if (passwordEncoder.matches(newPassword, current.getPassword())) {
            throw new IllegalArgumentException("La nueva contraseña debe ser distinta a la actual");
        }

        current.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(current);
    }

    @Override
    @Transactional
    public void deactivateOwnAccount(User user, AccountDeactivationRequest request) {
        User current = findById(user.getId());
        verifyPassword(current, request.getPassword());
        deactivate(current);
    }

    @Override
    @Transactional
    public void deactivateUser(Long userId) {
        User user = findById(userId);
        if (!user.isActive()) {
            throw new IllegalArgumentException("El usuario ya está dado de baja");
        }
        deactivate(user);
    }

    @Override
    @Transactional
    public void activateUser(Long userId) {
        User user = findById(userId);
        if (user.isActive()) {
            throw new IllegalArgumentException("El usuario ya está activo");
        }
        user.setActive(true);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void assignRole(Long userId, Role role) {
        User user = findById(userId);
        user.getRoles().add(role);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void removeRole(Long userId, Role role) {
        User user = findById(userId);
        if (!user.getRoles().contains(role)) {
            throw new IllegalArgumentException("El usuario no tiene el rol " + role);
        }
        if (user.getRoles().size() == 1) {
            throw new IllegalArgumentException("El usuario debe tener al menos un rol");
        }
        user.getRoles().remove(role);
        userRepository.save(user);
    }

    private void deactivate(User user) {
        if (user.getRoles().contains(Role.ADMIN)
                && userRepository.countByRolesContainingAndActiveTrue(Role.ADMIN) <= 1) {
            throw new IllegalArgumentException("No se puede dar de baja al único administrador activo");
        }
        user.setActive(false);
        userRepository.save(user);
        productRepository.deactivateBySellerId(user.getId());
    }

    private void verifyPassword(User user, String password) {
        if (password == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidPasswordException("La contraseña actual es incorrecta");
        }
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
        response.setActive(user.isActive());
        return response;
    }
}
