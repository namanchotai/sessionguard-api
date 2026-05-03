package com.sessionguard.service;

import com.sessionguard.dto.request.*;
import com.sessionguard.dto.response.AuthResponse;
import com.sessionguard.entity.*;
import com.sessionguard.exception.*;
import com.sessionguard.repository.*;
import com.sessionguard.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final SessionRepository sessionRepo;
    private final RefreshTokenRepository refreshRepo;
    private final BlacklistedTokenRepository blacklistRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    public void register(RegisterRequest req) {

        if (userRepo.existsByEmail(req.getEmail()))
            throw new UserAlreadyExistsException("Email exists");

        userRepo.save(User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .build());
    }

    public AuthResponse login(LoginRequest req, HttpServletRequest http) {

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        User user = userRepo.findByEmail(req.getEmail()).orElseThrow();

        Session session = sessionRepo.save(Session.builder()
                .user(user)
                .deviceInfo(http.getHeader("User-Agent"))
                .ipAddress(http.getRemoteAddr())
                .build());

        String access = jwtUtil.generateAccessToken(user.getEmail(), session.getId());
        String refresh = jwtUtil.generateRefreshToken(user.getEmail(), session.getId());

        refreshRepo.save(RefreshToken.builder()
                .token(refresh)
                .user(user)
                .session(session)
                .expiryDate(Instant.now().plusSeconds(7 * 24 * 60 * 60))
                .build());

        return AuthResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .tokenType("Bearer")
                .expiresIn(900)
                .sessionId(session.getId())
                .build();
    }

    public AuthResponse refresh(RefreshTokenRequest req) {

        jwtUtil.validateToken(req.getRefreshToken());

        RefreshToken stored = refreshRepo.findByToken(req.getRefreshToken())
                .orElseThrow(() -> new TokenInvalidException("Invalid refresh"));

        if (stored.isExpired()) {
            refreshRepo.delete(stored);
            throw new TokenExpiredException("Refresh expired");
        }

        String email = stored.getUser().getEmail();
        Long sessionId = stored.getSession().getId();

        refreshRepo.delete(stored);

        String newAccess = jwtUtil.generateAccessToken(email, sessionId);
        String newRefresh = jwtUtil.generateRefreshToken(email, sessionId);

        refreshRepo.save(RefreshToken.builder()
                .token(newRefresh)
                .user(stored.getUser())
                .session(stored.getSession())
                .expiryDate(Instant.now().plusSeconds(7 * 24 * 60 * 60))
                .build());

        return AuthResponse.builder()
                .accessToken(newAccess)
                .refreshToken(newRefresh)
                .tokenType("Bearer")
                .expiresIn(900)
                .sessionId(sessionId)
                .build();
    }

    public void logout(String accessToken) {

        Long sessionId;

        try {
            sessionId = jwtUtil.extractSessionId(accessToken);
        } catch (Exception e) {
            throw new TokenInvalidException("Invalid token");
        }

        Session session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException("Session not found"));

        // deactivate session
        session.setActive(false);
        sessionRepo.save(session);

        // delete refresh token safely
        refreshRepo.findBySession(session)
                .ifPresent(refreshRepo::delete);

        // blacklist access token
        blacklistRepo.save(BlacklistedToken.builder()
                .token(accessToken)
                .expiry(Instant.now().plusSeconds(900))
                .build());
    }

    public void logoutAll(String email) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new TokenInvalidException("User not found"));

        var sessions = sessionRepo.findByUser(user);

        for (Session session : sessions) {
            session.setActive(false);

            refreshRepo.findBySession(session)
                    .ifPresent(refreshRepo::delete);
        }

        sessionRepo.saveAll(sessions);
    }
}