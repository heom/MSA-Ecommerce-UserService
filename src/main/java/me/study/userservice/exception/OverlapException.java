package me.study.userservice.exception;

public class OverlapException extends RuntimeException {

    public OverlapException(String key, Object value) {
        super(String.format("%s [%s] is exists", key, value));
    }
}
