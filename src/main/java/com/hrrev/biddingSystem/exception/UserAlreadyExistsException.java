package com.hrrev.biddingSystem.exception;

public class UserAlreadyExistsException extends ApplicationException {
    public UserAlreadyExistsException(String message, String errorCode) {
        super(message, errorCode);
    }
}
