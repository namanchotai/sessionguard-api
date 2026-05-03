package com.sessionguard.repository;

import com.sessionguard.entity.Session;
import com.sessionguard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {

    List<Session> findByUserAndIsActiveTrue(User user);

    Optional<Session> findByIdAndUser(Long id, User user);

    List<Session> findByUser(User user);
}