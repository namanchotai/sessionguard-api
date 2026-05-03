package com.sessionguard.controller;

import com.sessionguard.dto.request.*;
import com.sessionguard.dto.response.*;
import com.sessionguard.exception.TokenInvalidException;
import com.sessionguard.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest req) {

        authService.register(req);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(null, "Registered", HttpStatus.CREATED));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid
            @RequestBody LoginRequest req,
            HttpServletRequest http) {

        return ResponseEntity.ok(
                ApiResponse.success(authService.login(req, http), "Login success", HttpStatus.OK)
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest req) {

        return ResponseEntity.ok(
                ApiResponse.success(authService.refresh(req), "Refreshed", HttpStatus.OK)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Authorization") String header) {

        String token = header.substring(7);

        authService.logout(token);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Logged out", HttpStatus.OK)
        );
    }

    @PostMapping("/logout-all")
    public ResponseEntity<ApiResponse<Void>> logoutAll(Authentication auth) {

        if (auth == null || !auth.isAuthenticated()) {
            throw new TokenInvalidException("Unauthorized");
        }

        authService.logoutAll(auth.getName());

        return ResponseEntity.ok(
                ApiResponse.success(null, "Logged out from all devices", HttpStatus.OK)
        );
    }
}