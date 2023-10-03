package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.utilities.models.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ItemDto {
    /**
     * Наименование вещи.
     */
    @NotBlank(groups = Marker.OnCreate.class)
    private String name;
    /**
     * Описание вещи.
     */
    @NotBlank(groups = Marker.OnCreate.class)
    private String description;
    /**
     * Доступна ли вещь к аренде.
     */
    @NotNull(groups = Marker.OnCreate.class)
    private Boolean available;
    /**
     * Идентификатор вещи.
     */
    private Long id;
    private Long requestId;
}
