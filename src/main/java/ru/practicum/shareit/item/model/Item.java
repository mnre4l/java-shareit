package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

/**
 * Класс предназначен для описания модели арендуемой вещи.
 */
@Data
public class Item {
    /**
     * Наименование вещи.
     */
    private String name;
    /**
     * Описание вещи.
     */
    private String description;
    /**
     * Пользователь, которому принадлежит вещь.
     */
    private User owner;
    /**
     * Доступна ли вещь к аренде.
     */
    private Boolean available;
    /**
     * Идентификатор вещи.
     */
    private Long id;

}
