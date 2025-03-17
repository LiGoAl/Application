package com.example.springbootguide.exception;

public class ResourceAlreadyOccupiedException extends RuntimeException {
    public ResourceAlreadyOccupiedException(String message) {
        super(message);
    }
}
