package com._3d.marketplace.controllers.config;

import com._3d.marketplace.entity.Role;
import com._3d.marketplace.entity.User;
import com._3d.marketplace.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@marketplace.com}")
    private String adminEmail;

    @Value("${app.admin.password:admin1234}")
    private String adminPassword;

    @Bean
    public CommandLineRunner seedAdmin() {
        return args -> {
            if (userRepository.findByEmail(adminEmail).isPresent()) {
                return;
            }

            User admin = User.builder()
                    .name("Admin")
                    .surname("Marketplace")
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .roles(Collections.singleton(Role.ADMIN))
                    .build();

            userRepository.save(admin);
            System.out.println(">> ADMIN inicial creado: " + adminEmail);
        };
    }
}
