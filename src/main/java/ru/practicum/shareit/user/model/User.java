package ru.practicum.shareit.user.model;

import lombok.Data;

import javax.persistence.*;

/**
 * Класс, описывающий модель пользователя.
 */
@Data
@Entity
@Table(name = "users", schema = "public")
public class User {
    /**
     * Идентификатор пользователя.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * Имя пользователя.
     */
    @Column(name = "name", nullable = false)
    private String name;
    /**
     * E-mail пользователя.
     */
    @Column(name = "email", nullable = false)
    private String email;
}
