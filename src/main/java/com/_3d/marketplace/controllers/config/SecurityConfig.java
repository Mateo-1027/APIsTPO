package com._3d.marketplace.controllers.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthFilter;
        private final AuthenticationProvider authenticationProvider;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .authorizeHttpRequests(req -> req
                                                .requestMatchers("/api/v1/auth/**").permitAll()

                                                // Endpoints específicos de vendedor (antes que los GET públicos)
                                                .requestMatchers(HttpMethod.GET, "/products/mine")
                                                .hasAnyRole("ADMIN", "VENDOR")
                                                .requestMatchers(HttpMethod.POST, "/products/estimate-price")
                                                .hasAnyRole("ADMIN", "VENDOR")

                                                .requestMatchers(HttpMethod.GET, "/products", "/products/**")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.GET, "/categories", "/categories/**")
                                                .permitAll()

                                                .requestMatchers(HttpMethod.POST, "/products").hasAnyRole("ADMIN", "VENDOR")
                                                .requestMatchers(HttpMethod.PUT, "/products/**").hasAnyRole("ADMIN", "VENDOR")
                                                .requestMatchers(HttpMethod.PATCH, "/products/**").hasAnyRole("ADMIN", "VENDOR")
                                                .requestMatchers(HttpMethod.DELETE, "/products/**").hasAnyRole("ADMIN", "VENDOR")

                                                .requestMatchers(HttpMethod.POST, "/categories").hasRole("ADMIN")

                                                .requestMatchers("/cart/**", "/orders/**").authenticated()

                                                .anyRequest().authenticated())
                                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}
