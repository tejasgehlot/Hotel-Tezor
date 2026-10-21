package org.example.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * WebUtil: Utility class for common web operations like date parsing, validation, and formatting.
 * Centralized place for business logic helpers used across servlets.
 */
public class WebUtil {

    /**
     * Parses a date string in YYYY-MM-DD format to a LocalDate object.
     * Returns null if the format is invalid.
     *
     * @param dateStr the date string to parse
     * @return LocalDate object or null if parsing fails
     */
    public static LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Parses an integer string to an int.
     * Returns -1 if parsing fails or the value is invalid.
     *
     * @param intStr the integer string to parse
     * @return parsed int or -1 if parsing fails
     */
    public static int parseInt(String intStr) {
        try {
            return Integer.parseInt(intStr);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Validates a stay (check-in and check-out dates with guest count).
     * Rules: check_in not in past, check_out after check_in, guests 1..4.
     *
     * @param checkIn the check-in date
     * @param checkOut the check-out date
     * @param guests the number of guests
     * @return error message if invalid, or null if valid
     */
    public static String validateStay(LocalDate checkIn, LocalDate checkOut, int guests) {
        LocalDate today = LocalDate.now();

        // Check if check-in is in the past
        if (checkIn.isBefore(today)) {
            return "Check-in date cannot be in the past.";
        }

        // Check if check-out is after check-in
        if (!checkOut.isAfter(checkIn)) {
            return "Check-out date must be after check-in date.";
        }

        // Check guest count
        if (guests < 1 || guests > 4) {
            return "Guests must be between 1 and 4.";
        }

        return null; // Valid
    }

    /**
     * Calculates the number of nights between check-in and check-out.
     *
     * @param checkIn the check-in date
     * @param checkOut the check-out date
     * @return number of nights
     */
    public static long calculateNights(LocalDate checkIn, LocalDate checkOut) {
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    /**
     * Validates a customer name (1..100 characters).
     *
     * @param name the name to validate
     * @return error message if invalid, or null if valid
     */
    public static String validateName(String name) {
        if (name == null || name.trim().isEmpty() || name.length() > 100) {
            return "Name must be between 1 and 100 characters.";
        }
        return null; // Valid
    }

    /**
     * Validates a phone number (10..15 digits, can include + prefix).
     * Regex: [0-9+]{10,15}
     *
     * @param phone the phone number to validate
     * @return error message if invalid, or null if valid
     */
    public static String validatePhone(String phone) {
        if (phone == null || !phone.matches("[0-9+]{10,15}")) {
            return "Phone must be 10-15 digits (can include + prefix).";
        }
        return null; // Valid
    }

    /**
     * Creates an error message response for display in JSP.
     *
     * @param message the error message
     * @return formatted error string
     */
    public static String showError(String message) {
        return message;
    }
}
