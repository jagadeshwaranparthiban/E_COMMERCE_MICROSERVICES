package com.jagadesh.userservice.exception;

public class NonRetryableEventException extends RuntimeException {
    public NonRetryableEventException(String message) {
        super(message);
    }
}
