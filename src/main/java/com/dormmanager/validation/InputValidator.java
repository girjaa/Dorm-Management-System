package com.dormmanager.validation;

import com.dormmanager.exception.InvalidInputException;

/**
 * Utility class for validating all user input before it enters the program.
 * Requirement: "All input from the user validated before it enters the program."
 */
public final class InputValidator {

    private InputValidator() {
        // utility class — no instantiation
    }

    /**
     * Validates that a string is not null or blank.
     *
     * @param value     the value to check
     * @param fieldName the name of the field (for error messages)
     * @throws InvalidInputException if the value is null or blank
     */
    public static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new InvalidInputException(fieldName + " cannot be empty.");
        }
    }

    /**
     * Validates that a number is positive.
     *
     * @param value     the number to check
     * @param fieldName the name of the field
     * @throws InvalidInputException if the value is not positive
     */
    public static void validatePositiveNumber(int value, String fieldName) {
        if (value <= 0) {
            throw new InvalidInputException(fieldName + " must be a positive number. Got: " + value);
        }
    }

    /**
     * Validates an email address with a simple regex.
     *
     * @param email the email to check
     * @throws InvalidInputException if the email format is invalid
     */
    public static void validateEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new InvalidInputException("Invalid email format: " + email);
        }
    }

    /**
     * Validates that a room number is between 1 and 999.
     *
     * @param roomNumber the room number to check
     * @throws InvalidInputException if outside range
     */
    public static void validateRoomNumber(int roomNumber) {
        if (roomNumber < 1 || roomNumber > 999) {
            throw new InvalidInputException("Room number must be between 1 and 999. Got: " + roomNumber);
        }
    }

    /**
     * Validates a complaint description (not empty, more than 10 characters).
     *
     * @param description the description to check
     * @throws InvalidInputException if too short or empty
     */
    public static void validateComplaintDescription(String description) {
        validateNotEmpty(description, "Complaint description");
        if (description.trim().length() <= 10) {
            throw new InvalidInputException(
                    "Complaint description must be more than 10 characters. Got: " + description.length());
        }
    }

    /**
     * Validates a student ID (alphanumeric, 3-20 characters).
     *
     * @param id the student ID to check
     * @throws InvalidInputException if format is invalid
     */
    public static void validateStudentId(String id) {
        validateNotEmpty(id, "Student ID");
        if (!id.matches("^[A-Za-z0-9]{3,20}$")) {
            throw new InvalidInputException(
                    "Student ID must be 3-20 alphanumeric characters. Got: '" + id + "'");
        }
    }

    /**
     * Sanitizes input by trimming whitespace and removing potentially dangerous characters.
     *
     * @param input the raw input
     * @return the sanitized input
     */
    public static String sanitizeInput(String input) {
        if (input == null) return "";
        return input.trim()
                .replace("|", "")   // pipe is our CSV delimiter
                .replace("<", "")
                .replace(">", "")
                .replace("&", "");
    }

    /**
     * Parses an integer from user input with a descriptive error.
     *
     * @param input     the raw string
     * @param fieldName the field name for error messages
     * @return the parsed integer
     * @throws InvalidInputException if the input is not a valid integer
     */
    public static int parseInteger(String input, String fieldName) {
        try {
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            throw new InvalidInputException(fieldName + " must be a number. Got: '" + input + "'");
        }
    }
}
