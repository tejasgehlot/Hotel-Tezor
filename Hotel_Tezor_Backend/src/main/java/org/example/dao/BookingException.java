package org.example.dao;

/**
 * BookingException: Checked exception for booking-related errors (invalid room, capacity, availability, etc).
 */
public class BookingException extends Exception {
    public BookingException(String message) {
        super(message);
    }
}
