package com.dormmanager.exception;

/**
 * Thrown when data cannot be loaded from or saved to persistent storage.
 */
public class DataLoadException extends RuntimeException {
    public DataLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
