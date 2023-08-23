package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.user.model.User;

/**
 * Класс предназначен для маппинга моделей вещей
 */
@Component
@RequiredArgsConstructor
public class ItemDtoMapper {
    /**
     * Маппер
     */
    private final ModelMapper mapper;

    /**
     * Метод предназначен для маппинга Item -> ItemDto
     * @param item Item-объект
     * @return ItemDto-объект
     */
    public ItemDto toDto(Item item) {
        return mapper.map(item, ItemDto.class);
    }

    /**
     * Метод предназначен для маппинга ItemDto -> Item
     * @param itemDto ItemDto-объект
     * @return Item-объект
     */
    public Item fromDto(ItemDto itemDto) {
        return mapper.map(itemDto, Item.class);
    }

    /**
     * Метод предназначен для маппинга ItemDto -> уже созданный Item
     * @param itemDto ItemDto-объект
     * @param item уже созданный Item-объект
     */
    public void fromDto(ItemDto itemDto, Item item) {
        mapper.map(itemDto, item);
    }

    /**
     * Метод используется при создании вещей с целью установки пользователя-создателя в поле к вещи
     * @param itemDto ItemDto-объект вещи
     * @param owner Объект пользователя
     * @return Item с установленным полем User-создателя (owner)
     */
    public Item fromDto(ItemDto itemDto, User owner) {
        Item item = fromDto(itemDto);

        item.setOwner(owner);
        return item;
    }
}
