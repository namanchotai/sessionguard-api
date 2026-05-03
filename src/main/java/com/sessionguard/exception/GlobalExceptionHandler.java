package com.sessionguard.exception;

import com.sessionguard.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiResponse<Void>> handleExpired(TokenExpiredException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ex.getMessage(), ErrorCode.TOKEN_EXPIRED.name(), HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(TokenInvalidException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalid(TokenInvalidException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ex.getMessage(), ErrorCode.TOKEN_INVALID.name(), HttpStatus.FORBIDDEN));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleUser(UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage(), ErrorCode.USER_ALREADY_EXISTS.name(), HttpStatus.CONFLICT));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAll(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Something went wrong", ErrorCode.INTERNAL_ERROR.name(), HttpStatus.INTERNAL_SERVER_ERROR));
    }
}