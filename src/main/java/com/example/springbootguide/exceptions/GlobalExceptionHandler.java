package com.example.springbootguide.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(AuthenticationException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("status", HttpStatus.UNAUTHORIZED.value());
        errors.put("error", "No authorized user");
        errors.put("message", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("status", HttpStatus.BAD_REQUEST.value());
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("status", HttpStatus.BAD_REQUEST.value());
        errors.put("error", "Malformed JSON request");
        errors.put("message", "Please ensure that your request body is properly structured and valid JSON.");
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("status", HttpStatus.NOT_FOUND.value());
        errors.put("error", "Resource not found");
        errors.put("message", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceAlreadyOccupiedException.class)
    public ResponseEntity<Map<String, Object>> handleResourceAlreadyOccupied(ResourceAlreadyOccupiedException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("status", HttpStatus.CONFLICT.value());
        errors.put("error", "Resource already occupied");
        errors.put("message", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RequestValidationException.class)
    public ResponseEntity<Map<String, Object>> handleRequestValidation(RequestValidationException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("status", HttpStatus.BAD_REQUEST.value());
        errors.put("error", "Invalid request");
        errors.put("message", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllException(Exception ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errors.put("error", "An unexpected error occurred");
        errors.put("message", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
