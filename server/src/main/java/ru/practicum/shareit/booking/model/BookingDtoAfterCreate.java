package ru.practicum.shareit.booking.model;

import lombok.Data;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.user.model.UserDto;

import java.time.LocalDateTime;

@Data
public class BookingDtoAfterCreate {
    Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    Status status;
    ItemDto item;
    UserDto booker;
}
