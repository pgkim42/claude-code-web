package com.example.bookmark.resolver;

import com.example.bookmark.dto.AuthPayload;
import com.example.bookmark.dto.LoginInput;
import com.example.bookmark.dto.SignupInput;
import com.example.bookmark.model.User;
import com.example.bookmark.service.AuthService;
import com.example.bookmark.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * GraphQL resolver for user and authentication operations
 */
@Controller
@RequiredArgsConstructor
public class UserResolver {

    private final AuthService authService;
    private final UserService userService;

    /**
     * Get current authenticated user
     * @return current user or null
     */
    @QueryMapping
    public User currentUser() {
        return authService.getCurrentUser();
    }

    /**
     * Get all users (admin only)
     * @return list of all users
     */
    @QueryMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> users() {
        return userService.getAllUsers();
    }

    /**
     * Get user by ID (admin only)
     * @param id user ID
     * @return user if found
     */
    @QueryMapping
    @PreAuthorize("hasRole('ADMIN')")
    public User user(@Argument Long id) {
        return userService.getUserById(id).orElse(null);
    }

    /**
     * Register a new user
     * @param input signup input data
     * @return authentication payload
     */
    @MutationMapping
    public AuthPayload signup(@Argument SignupInput input) {
        return authService.signup(input);
    }

    /**
     * Login user
     * @param input login input data
     * @param request HTTP request for session management
     * @param response HTTP response for session management
     * @return authentication payload
     */
    @MutationMapping
    public AuthPayload login(
            @Argument LoginInput input,
            HttpServletRequest request,
            HttpServletResponse response) {
        return authService.login(input, request, response);
    }

    /**
     * Logout current user
     * @param request HTTP request for session management
     * @param response HTTP response for session management
     * @return true if logout successful
     */
    @MutationMapping
    public boolean logout(HttpServletRequest request, HttpServletResponse response) {
        return authService.logout(request, response);
    }
}
