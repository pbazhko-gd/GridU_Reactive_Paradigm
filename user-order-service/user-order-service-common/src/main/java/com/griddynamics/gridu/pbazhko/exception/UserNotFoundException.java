package com.griddynamics.gridu.pbazhko.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String userId) {
        super("Unknown userId: " + userId);
    }
}
