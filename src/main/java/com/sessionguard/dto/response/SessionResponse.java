package com.sessionguard.dto.response;

import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionResponse {

    private Long sessionId;
    private String deviceInfo;
    private String ipAddress;
    private Instant loginTime;
    private boolean active;
}