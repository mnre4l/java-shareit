package ru.practicum.shareit.booking.model;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class BookingDtoOnCreate {
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;

}
