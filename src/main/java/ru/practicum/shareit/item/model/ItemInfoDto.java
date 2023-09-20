package ru.practicum.shareit.item.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemInfoDto {
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
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<Comment> comments;

    @Data
    public static class BookingDto {
        Long id;
        LocalDateTime start;
        LocalDateTime end;
        Long bookerId;
    }
}
