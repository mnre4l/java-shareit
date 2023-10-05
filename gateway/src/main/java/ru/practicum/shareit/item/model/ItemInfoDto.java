package ru.practicum.shareit.item.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
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
    private List<CommentDto> comments;

    @Getter
    @Setter
    public static class BookingDto {
        Long id;
        LocalDateTime start;
        LocalDateTime end;
        Long bookerId;
    }

    @Override
    public String toString() {
        return "ItemInfoDto{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", available=" + available +
                ", id=" + id +
                ", lastBooking=" + lastBooking +
                ", nextBooking=" + nextBooking +
                '}';
    }
}
