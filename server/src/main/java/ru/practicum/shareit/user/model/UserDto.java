package ru.practicum.shareit.user.model;

import lombok.Data;

@Data
public class UserDto {
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
