package ru.practicum.shareit.booking.model;


import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;

/**
 * Класс-модель бронирования
 */
public class Booking {
    /**
     * Идентефикатор брони
     */
    private Long id;
    /**
     * Время начала брони
     */
    private Instant start;
    /**
     * Время конца брони
     */
    private Instant end;
    /**
     * Что бронируется (объект типа Item)
     */
    private Item item;
    /**
     * Кто бронирует (объект типа User)
     */
    private User booker;
    /**
     * Статус брони
     */
    private Status status;
}
