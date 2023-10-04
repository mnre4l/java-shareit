package ru.practicum.shareit.item.model;

import lombok.Data;

@Data
public class ItemDto {
    /**
     * Наименование вещи.
     */
    private String name;
    /**
     * Описание вещи.
     */
    private String description;
    /**
     * Доступна ли вещь к аренде.
     */
    private Boolean available;
    /**
     * Идентификатор вещи.
     */
    private Long id;
    private Long requestId;
}
