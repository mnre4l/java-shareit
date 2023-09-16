package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

/**
 * Класс предназначен для описания модели арендуемой вещи.
 */
@Data
@Entity
@Table(name = "items", schema = "public")
public class Item {
    /**
     * Наименование вещи.
     */
    @Column(name = "name", nullable = false)
    private String name;
    /**
     * Описание вещи.
     */
    @Column(name = "description", nullable = false)
    private String description;
    /**
     * Пользователь, которому принадлежит вещь.
     */
    @ManyToOne
    private User owner;
    /**
     * Доступна ли вещь к аренде.
     */
    @Column(name = "available", nullable = false)
    private Boolean available;
    /**
     * Идентификатор вещи.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
