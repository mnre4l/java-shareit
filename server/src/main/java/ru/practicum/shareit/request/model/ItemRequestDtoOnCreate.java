package ru.practicum.shareit.request.model;


import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ItemRequestDtoOnCreate {
    private String description;

    @Override
    public String toString() {
        return "ItemRequestDtoOnCreate{" +
                "description='" + description + '\'' +
                '}';
    }
}
