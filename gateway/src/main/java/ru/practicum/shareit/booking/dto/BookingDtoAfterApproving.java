package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookingDtoAfterApproving {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;
    private Booker booker;
    private Item item;


    @Getter
    @Setter
    public static class Booker {
        long id;
    }

    @Getter
    @Setter
    public static class Item {
        long id;
        String name;
    }
}
