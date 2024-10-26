package com.hrrev.biddingSystem.exception;

public class UnauthorizedException extends ApplicationException {
    public UnauthorizedException(String message, String errorCode) {
        super(message, errorCode);
    }
}

