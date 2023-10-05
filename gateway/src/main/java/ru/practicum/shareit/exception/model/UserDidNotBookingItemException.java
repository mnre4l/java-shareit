package ru.practicum.shareit.exception.model;

public class UserDidNotBookingItemException extends RuntimeException {
    public UserDidNotBookingItemException(String message) {
        super(message);
    }
}
