package com.sessionguard.service;

import com.sessionguard.dto.response.SessionResponse;
import com.sessionguard.entity.Session;
import com.sessionguard.entity.User;
import com.sessionguard.exception.SessionNotFoundException;
import com.sessionguard.exception.TokenInvalidException;
import com.sessionguard.repository.RefreshTokenRepository;
import com.sessionguard.repository.SessionRepository;
import com.sessionguard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepo;
    private final UserRepository userRepo;
    private final RefreshTokenRepository refreshRepo;

    public List<SessionResponse> getSessions(String email) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new TokenInvalidException("User not found"));

        return sessionRepo.findByUserAndIsActiveTrue(user)
                .stream()
                .map(s -> SessionResponse.builder()
                        .sessionId(s.getId())
                        .deviceInfo(s.getDeviceInfo())
                        .ipAddress(s.getIpAddress())
                        .loginTime(s.getCreatedAt())
                        .active(s.isActive())
                        .build())
                .toList();
    }

    public void deleteSession(Long id, String email) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Session session = sessionRepo.findByIdAndUser(id, user)
                .orElseThrow(() -> new SessionNotFoundException("Session not found"));

        session.setActive(false);
        sessionRepo.save(session);

        refreshRepo.findBySession(session)
                .ifPresent(refreshRepo::delete);
    }
}