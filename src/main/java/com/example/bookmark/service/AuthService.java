package com.example.bookmark.service;

import com.example.bookmark.dto.AuthPayload;
import com.example.bookmark.dto.LoginInput;
import com.example.bookmark.dto.SignupInput;
import com.example.bookmark.model.User;
import com.example.bookmark.model.UserRole;
import com.example.bookmark.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authentication service
 * Handles signup, login, and logout operations
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    /**
     * Register a new user
     * @param input signup input data
     * @return authentication payload
     */
    @Transactional
    public AuthPayload signup(SignupInput input) {
        // Validate username uniqueness
        if (userRepository.existsByUsername(input.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Validate email uniqueness
        if (userRepository.existsByEmail(input.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Create new user
        User user = new User();
        user.setUsername(input.getUsername());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setEmail(input.getEmail());
        user.setRole(UserRole.USER);
        user.setEnabled(true);

        User savedUser = userRepository.save(user);

        return new AuthPayload(savedUser, "User registered successfully");
    }

    /**
     * Login user with session-based authentication
     * @param input login input data
     * @param request HTTP request for session management
     * @param response HTTP response for session management
     * @return authentication payload
     */
    @Transactional(readOnly = true)
    public AuthPayload login(LoginInput input, HttpServletRequest request, HttpServletResponse response) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getUsername(),
                        input.getPassword()
                )
        );

        // Set authentication in security context
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // Save security context to session
        securityContextRepository.saveContext(context, request, response);

        // Get user
        User user = userRepository.findByUsername(input.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return new AuthPayload(user, "Login successful");
    }

    /**
     * Logout current user
     * @param request HTTP request for session management
     * @param response HTTP response for session management
     * @return true if logout successful
     */
    public boolean logout(HttpServletRequest request, HttpServletResponse response) {
        // Clear security context
        SecurityContextHolder.clearContext();

        // Invalidate session
        if (request.getSession(false) != null) {
            request.getSession().invalidate();
        }

        return true;
    }

    /**
     * Get current authenticated user
     * @return current user or null if not authenticated
     */
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
            "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username).orElse(null);
    }
}
