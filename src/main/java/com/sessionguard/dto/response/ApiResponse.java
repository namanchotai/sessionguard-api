package com.sessionguard.dto.response;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private String errorCode;
    private int httpStatus;
    private Instant timestamp;

    public static <T> ApiResponse<T> success(T data, String message, HttpStatus status) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .httpStatus(status.value())
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String message, String errorCode, HttpStatus status) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .httpStatus(status.value())
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String message, String errorCode, HttpStatus status, T data) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .httpStatus(status.value())
                .data(data)
                .timestamp(Instant.now())
                .build();
    }
}