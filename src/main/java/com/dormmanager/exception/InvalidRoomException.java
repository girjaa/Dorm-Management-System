package com.dormmanager.exception;

/**
 * Thrown when an invalid room number or room configuration is encountered.
 */
public class InvalidRoomException extends RuntimeException {
    public InvalidRoomException(String message) {
        super(message);
    }

    public InvalidRoomException(String message, Throwable cause) {
        super(message, cause);
    }
}
