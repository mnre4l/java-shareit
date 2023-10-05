package ru.practicum.shareit.exception.model;


import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс предназначен для формирования ответа в случае перехвата исключения.
 * Объект этого класса помещается в тело ответа в случае перехвата исключения.
 */
@Data
@NoArgsConstructor
public class ErrorResponse {
    private String error;
    private StackTraceElement[] stackTraceElements;

    /**
     * @param message сообщение об исключении.
     */
    public ErrorResponse(String message) {
        this.error = message;
    }

    public ErrorResponse(String message, StackTraceElement[] stackTrace) {
        this.error = message;
        this.stackTraceElements = stackTrace;
    }
}