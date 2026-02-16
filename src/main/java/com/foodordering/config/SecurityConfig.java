package com.foodordering.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security Configuration
 * Simplified for demo purposes
 * In production, implement proper JWT authentication
 *
 * @author Yanamala Sanjay
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configure security filter chain
     * For demo, we're disabling CSRF and allowing all requests
     * In production, implement proper authentication
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Disable CSRF for REST API
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()  // Allow all requests for demo
                );

        return http.build();
    }

    /**
     * Password encoder bean
     * Uses BCrypt hashing algorithm
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

/**
 * Interview Discussion Points:
 *
 * Q: Why disable CSRF?
 * A: CSRF protection is for browser-based form submissions.
 *    For REST APIs consumed by mobile/web apps with JWT tokens,
 *    CSRF is not applicable. The token itself provides protection.
 *
 * Q: How would you implement production-ready security?
 * A: 1. JWT-based authentication
 *    2. Role-based access control (CUSTOMER, KITCHEN_STAFF, ADMIN)
 *    3. Rate limiting to prevent abuse
 *    4. HTTPS only in production
 *    5. Input validation and sanitization
 *    6. Secure password storage with BCrypt
 *
 * Q: What about password security?
 * A: Using BCryptPasswordEncoder with salt and adaptive hashing.
 *    Never store plain text passwords.
 */
