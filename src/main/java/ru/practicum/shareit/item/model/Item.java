package ru.practicum.shareit.item.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.List;

/**
 * Класс предназначен для описания модели арендуемой вещи.
 */
@Getter
@Setter
@Entity
@Table(name = "items", schema = "public")
@NamedEntityGraph(name = "item-comments-graph", attributeNodes = {@NamedAttributeNode("comments")})
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
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id")
    private List<Comment> comments;
}
