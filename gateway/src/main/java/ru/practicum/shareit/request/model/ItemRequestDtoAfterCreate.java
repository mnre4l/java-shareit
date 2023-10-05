package ru.practicum.shareit.request.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ItemRequestDtoAfterCreate {
    private String description;
    private Long id;
    private LocalDateTime created;
}
