package ru.practicum.shareit.item.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "comments", schema = "public")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    @Column(name = "author_name", nullable = false)
    private String authorName;
    private LocalDateTime created;
    @Column(name = "item_id")
    private Long itemId;
}
