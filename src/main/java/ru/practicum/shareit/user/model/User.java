package ru.practicum.shareit.user.model;

import lombok.Data;

/**
 * Класс, описывающий модель пользователя.
 */
@Data
public class User {
    /**
     * Идентификатор пользователя.
     */
    private Long id;
    /**
     * Имя пользователя.
     */
    private String name;
    /**
     * E-mail пользователя.
     */
    private String email;
}
