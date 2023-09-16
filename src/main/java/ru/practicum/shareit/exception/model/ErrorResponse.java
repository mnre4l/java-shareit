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
    private String message;
    private StackTraceElement[] stackTraceElements;

    /**
     * @param message сообщение об исключении.
     */
    public ErrorResponse(String message) {
        this.message = message;
    }

    public ErrorResponse(String message, StackTraceElement[] stackTrace) {
        this.message = message;
        this.stackTraceElements = stackTrace;
    }
}