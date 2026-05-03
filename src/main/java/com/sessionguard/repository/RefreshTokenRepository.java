package com.sessionguard.repository;

import com.sessionguard.entity.RefreshToken;
import com.sessionguard.entity.Session;
import com.sessionguard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findBySession(Session session);

    void deleteBySession(Session session);

    void deleteByUser(User user);
}