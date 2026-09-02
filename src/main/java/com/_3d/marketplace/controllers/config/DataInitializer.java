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

/**
 * Crea un usuario ADMIN al arrancar la app si todavía no existe.
 * Resuelve el problema del "huevo y la gallina": sin esto no habría forma
 * de que nazca el primer administrador (el registro solo da USER o VENDOR).
 *
 * Las credenciales se configuran en application.properties.
 */
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
                return; // el admin ya existe, no hacemos nada
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
