package com.maybank.transaction.controller;

import com.maybank.transaction.service.AuthService;
import com.maybank.transaction.service.BankUserDetailsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    @Autowired
    private BankUserDetailsService userDetailsService;
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        log.trace("User {} trying to login", request.username());
        String token = authService.getAuthToken(request.username(), request.password());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        userDetailsService.registerUser(request.username(), request.password());
        log.trace("User {} registered successfully", request.username());
        return ResponseEntity.ok("User registered successfully");
    }

    public record LoginRequest(
            @NotBlank(message = "Username cannot be empty")
            String username,

            @NotBlank(message = "Password cannot be empty")
            String password
    ) {}

    public record RegisterRequest(
            @NotBlank(message = "Username cannot be empty")
            @Size(min = 4, max = 20, message = "Username must be 4-20 characters")
            String username,

            @NotBlank(message = "Password cannot be empty")
            @Size(min = 8, message = "Password must be at least 8 characters")
            String password
    ) {}
    public record AuthResponse(String token) {}
}