package ru.practicum.shareit.request.model;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class ItemRequestDtoOnCreate {
    @NotBlank
    private String description;

    @Override
    public String toString() {
        return "ItemRequestDtoOnCreate{" +
                "description='" + description + '\'' +
                '}';
    }
}
