package ru.practicum.shareit.exception.model;

/**
 * Исключение, связанное с нарушением логики "пользователь является владельцем вещи"
 */
public class UserIsNotItemOwnerException extends RuntimeException {
    /**
     * @param message сообщение, содержащие информацию об ошибке.
     */
    public UserIsNotItemOwnerException(String message) {
        super(message);
    }
}
