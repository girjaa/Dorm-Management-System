package com.dormmanager.exception;

/**
 * Thrown when a student cannot be found by ID.
 */
public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(String message) {
        super(message);
    }
}
