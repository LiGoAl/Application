package com.example.springbootguide.exceptions;

public class ResourceAlreadyOccupiedException extends RuntimeException {
    public ResourceAlreadyOccupiedException(String message) {
        super(message);
    }
}
