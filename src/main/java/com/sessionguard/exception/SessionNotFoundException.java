package com.sessionguard.exception;

public class SessionNotFoundException extends RuntimeException {
    public SessionNotFoundException(String msg) { super(msg); }
}