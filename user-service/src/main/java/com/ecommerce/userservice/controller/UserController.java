package com.ecommerce.userservice.controller;

import com.ecommerce.userservice.dto.request.*;
import com.ecommerce.userservice.dto.response.*;
import com.ecommerce.userservice.service.impl.AuthServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Service",
        description = "Authentication and User Management")
public class UserController {

    private final AuthServiceImpl authService;

    @PostMapping("/register")
    @Operation(summary = "Register new user",
            description = "Register as CUSTOMER, SELLER or ADMIN")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(
                authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login",
            description = "Returns JWT token")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request) {
        return ResponseEntity.ok(
                authService.login(request));
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate JWT token",
            description = "Used by API Gateway")
    public ResponseEntity<UserResponse> validate(
            @RequestHeader("Authorization")
            String authHeader) {
        String token = authHeader.substring(7);
        return ResponseEntity.ok(
                authService.validateToken(token));
    }

    @GetMapping("/profile")
    @Operation(summary = "Get my profile")
    public ResponseEntity<UserResponse> getProfile() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return ResponseEntity.ok(
                authService.getProfile(email));
    }
}