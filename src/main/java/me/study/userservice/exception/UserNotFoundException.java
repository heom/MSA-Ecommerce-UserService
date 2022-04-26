package me.study.userservice.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String key, Object value) {
        super(String.format("User%s [%s] not found", key, value));
    }
}
