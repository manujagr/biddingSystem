package com.hrrev.biddingSystem.exception;

/**
 * Exception thrown when a bad request is made to the application.
 */
public class BadRequestException extends ApplicationException {
    public BadRequestException(String message, String errorCode) {
        super(message, errorCode);
    }
}
