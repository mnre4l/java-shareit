package ru.practicum.shareit.request.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ItemRequestDtoInfo {
    private String description;
    private LocalDateTime created;
    private List<ItemForRequestDto> items;
    private Long id;

    @Setter
    @Getter
    public static class ItemForRequestDto {
        private Long id;
        private String description;
        private Long requestId;
        private Boolean available;
        private String name;
    }
}
