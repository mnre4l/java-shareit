package ru.practicum.shareit.booking.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingDtoAfterApproving {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;
    private Booker booker;
    private Item item;


    @Data
    private static class Booker {
        long id;
    }

    @Data
    private static class Item {
        long id;
        String name;
    }
}
