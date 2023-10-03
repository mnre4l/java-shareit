package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDtoAfterCreate;
import ru.practicum.shareit.request.model.ItemRequestDtoInfo;
import ru.practicum.shareit.request.model.ItemRequestDtoOnCreate;

@Component
@Slf4j
@RequiredArgsConstructor
public class ItemRequestDtoMapper {
    private final ModelMapper mapper;

    public ItemRequest fromDto(ItemRequestDtoOnCreate itemRequestDtoOnCreate) {
        return mapper.map(itemRequestDtoOnCreate, ItemRequest.class);
    }

    public ItemRequestDtoAfterCreate fromDroAfterCreate(ItemRequest itemRequest) {
        return mapper.map(itemRequest, ItemRequestDtoAfterCreate.class);
    }

    public ItemRequestDtoInfo toDtoInfo(ItemRequest itemRequest) {
        return mapper.map(itemRequest, ItemRequestDtoInfo.class);
    }
}
