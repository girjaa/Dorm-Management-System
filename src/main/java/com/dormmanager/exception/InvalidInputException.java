package com.dormmanager.exception;

/**
 * Thrown when user input fails validation.
 */
public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message) {
        super(message);
    }
}
