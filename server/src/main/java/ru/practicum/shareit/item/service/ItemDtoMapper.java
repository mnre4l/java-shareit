package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.BookingDtoAfterApproving;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.ItemInfoDto;
import ru.practicum.shareit.request.model.ItemRequestDtoInfo;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс предназначен для маппинга моделей вещей
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ItemDtoMapper {
    /**
     * Маппер
     */
    private final ModelMapper mapper;

    /**
     * Метод предназначен для маппинга Item -> ItemDto
     *
     * @param item Item-объект
     * @return ItemDto-объект
     */
    public ItemDto toDto(Item item) {
        return mapper.map(item, ItemDto.class);
    }

    /**
     * Метод предназначен для маппинга ItemDto -> Item
     *
     * @param itemDto ItemDto-объект
     * @return Item-объект
     */
    public Item fromDto(ItemDto itemDto) {
        return mapper.map(itemDto, Item.class);
    }

    /**
     * Метод предназначен для маппинга ItemDto -> уже созданный Item
     *
     * @param itemDto ItemDto-объект
     * @param item    уже созданный Item-объект
     */
    public void fromDto(ItemDto itemDto, Item item) {
        mapper.map(itemDto, item);
    }

    /**
     * Метод используется при создании вещей с целью установки пользователя-создателя в поле к вещи
     *
     * @param itemDto ItemDto-объект вещи
     * @param owner   Объект пользователя
     * @return Item с установленным полем User-создателя (owner)
     */
    public Item fromDto(ItemDto itemDto, User owner) {
        Item item = fromDto(itemDto);

        item.setOwner(owner);
        return item;
    }

    public List<ItemInfoDto> toItemInfoDto(List<Item> items) {
        log.info("Список Item для маппинга в toItemInfoDto: {}", items);
        List<ItemInfoDto> mappedItems = items.stream()
                .map(item -> mapper.map(item, ItemInfoDto.class))
                .collect(Collectors.toList());
        log.info("Результат маппинга: {}", mappedItems);
        return mappedItems;
    }

    public ItemInfoDto toItemInfoDto(Item item) {
        log.info("Item для маппинга в toItemInfoDto: {}", item);
        return mapper.map(item, ItemInfoDto.class);
    }

    public ItemRequestDtoInfo.ItemForRequestDto toItemForRequestDto(Item item) {
        log.info("Item для маппинга в ItemRequestDtoInfo.ItemForRequestDto: {}", item);
        return mapper.map(item, ItemRequestDtoInfo.ItemForRequestDto.class);
    }

    public BookingDtoAfterApproving.Item toItemForBookingDto(Item item) {
        log.info("Item для маппинга в BookingDtoAfterApproving.Item: {}", item);
        return mapper.map(item, BookingDtoAfterApproving.Item.class);
    }
}
