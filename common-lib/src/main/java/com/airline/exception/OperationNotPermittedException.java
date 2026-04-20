package com.airline.exception;

public class OperationNotPermittedException extends Exception {
    public OperationNotPermittedException(String message) {
        super(message);
    }
}
