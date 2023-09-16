package ru.practicum.shareit.exception.model;

public class BadBookingStatusException extends RuntimeException {
    public BadBookingStatusException(String message) {
        super(message);
    }
}
