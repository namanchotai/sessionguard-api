package com.sessionguard.controller;

import com.sessionguard.dto.response.*;
import com.sessionguard.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getSessions(Authentication auth) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        sessionService.getSessions(auth.getName()),
                        "Sessions fetched",
                        HttpStatus.OK
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            Authentication auth) {

        sessionService.deleteSession(id, auth.getName());

        return ResponseEntity.ok(
                ApiResponse.success(null, "Deleted", HttpStatus.OK)
        );
    }
}