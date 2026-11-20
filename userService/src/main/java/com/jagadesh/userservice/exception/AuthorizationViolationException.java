package com.jagadesh.userservice.exception;

public class AuthorizationViolationException extends RuntimeException {
    public AuthorizationViolationException(String message) {
        super(message);
    }
}
