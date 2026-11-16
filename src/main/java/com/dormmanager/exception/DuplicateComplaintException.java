package com.dormmanager.exception;

/**
 * Thrown when a duplicate complaint is submitted (same room, same category, within short timeframe).
 */
public class DuplicateComplaintException extends RuntimeException {
    public DuplicateComplaintException(String message) {
        super(message);
    }

    public DuplicateComplaintException(String message, Throwable cause) {
        super(message, cause);
    }
}
