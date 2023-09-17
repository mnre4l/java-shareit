package ru.practicum.shareit.exception.model;

import lombok.Getter;

@Getter
public class BadBookingStatusException extends RuntimeException {
    public BadBookingStatusException(String message) {
        super(message);
    }
}
