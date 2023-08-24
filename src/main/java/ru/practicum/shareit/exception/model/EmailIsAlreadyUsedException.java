package ru.practicum.shareit.exception.model;

/**
 * Исключение, связанное с созадем пользователя, имейл которого уже занят.
 */
public class EmailIsAlreadyUsedException extends RuntimeException {
    /**
     * @param message сообщение, содержащие информацию об ошибке.
     */
    public EmailIsAlreadyUsedException(String message) {
        super(message);
    }
}
