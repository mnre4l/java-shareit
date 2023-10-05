package ru.practicum.shareit.exception.model;

public class StatusCanNotBeChangedException extends RuntimeException {
    public StatusCanNotBeChangedException(String message) {
        super(message);
    }
}
