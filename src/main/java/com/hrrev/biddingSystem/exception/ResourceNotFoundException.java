package com.hrrev.biddingSystem.exception;

public class ResourceNotFoundException extends ApplicationException {
    public ResourceNotFoundException(String message, String errorCode) {
        super(message, errorCode);
    }
}
