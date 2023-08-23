package ru.practicum.shareit.user.model;

import lombok.Data;
import ru.practicum.shareit.utilities.models.Marker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserDto {
    /**
     * Идентификатор пользователя.
     */
    private Long id;
    /**
     * Имя пользователя.
     */
    @NotBlank(groups = Marker.OnCreate.class)
    private String name;
    /**
     * E-mail пользователя.
     */
    @Email
    @NotNull(groups = Marker.OnCreate.class)
    private String email;
}
