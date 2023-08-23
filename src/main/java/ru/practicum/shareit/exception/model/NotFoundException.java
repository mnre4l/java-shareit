package ru.practicum.shareit.exception.model;

/**
 * Исключение, связанное с отсутствием в репозитории запрашиваемых сущностей.
 */
public class NotFoundException extends RuntimeException {
    /**
     * @param message сообщение, содержащие информацию об ошибке.
     */
    public NotFoundException(String message) {
        super(message);
    }
}
