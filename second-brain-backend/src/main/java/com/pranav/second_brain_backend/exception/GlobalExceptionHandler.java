package com.pranav.second_brain_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<?> handleBadRequest(BadRequestException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.of(
                                                "timestamp", LocalDateTime.now(),
                                                "error", ex.getMessage(),
                                                "status", 400));
        }

        @ExceptionHandler(UnauthorizedException.class)
        public ResponseEntity<?> handleUnauthorized(UnauthorizedException ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(Map.of(
                                                "timestamp", LocalDateTime.now(),
                                                "error", ex.getMessage(),
                                                "status", 401));
        }

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(Map.of(
                                                "timestamp", LocalDateTime.now(),
                                                "error", ex.getMessage(),
                                                "status", 404));
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
                Map<String, String> errors = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .collect(Collectors.toMap(
                                                error -> error.getField(),
                                                error -> error.getDefaultMessage(),
                                                (existing, replacement) -> existing));

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.of(
                                                "timestamp", LocalDateTime.now(),
                                                "error", "Validation failed",
                                                "status", 400,
                                                "details", errors));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<?> handleGeneric(Exception ex) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of(
                                                "timestamp", LocalDateTime.now(),
                                                "error", "Something went wrong",
                                                "status", 500));
        }

        // Add this method inside GlobalExceptionHandler class:
        @ExceptionHandler(IOException.class)
        public ResponseEntity<?> handleIO(IOException ex) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of(
                                                "timestamp", LocalDateTime.now(),
                                                "error", "Failed to process file: " + ex.getMessage(),
                                                "status", 500));
        }
}
