package ru.practicum.shareit.booking.model;


import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Класс-модель бронирования
 */
@Entity
@Data
@Table(name = "bookings", schema = "public")
public class Booking {
    /**
     * Идентефикатор брони
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * Время начала брони
     */
    @Column(name = "start_booking", nullable = false)
    private LocalDateTime start;
    /**
     * Время конца брони
     */
    @Column(name = "end_booking", nullable = false)
    private LocalDateTime end;
    /**
     * Что бронируется (объект типа Item)
     */
    @ManyToOne
    private Item item;
    /**
     * Кто бронирует (объект типа User)
     */
    @ManyToOne
    private User booker;
    /**
     * Статус брони
     */
    @Enumerated(EnumType.STRING)
    private Status status;
}
