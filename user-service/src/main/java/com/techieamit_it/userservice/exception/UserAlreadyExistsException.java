package com.techieamit_it.userservice.exception;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }

    public UserAlreadyExistsException(String username, String email) {
        super("User already exists with username: " + username + " or email: " + email);
    }
}