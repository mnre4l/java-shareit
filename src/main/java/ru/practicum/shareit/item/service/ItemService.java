package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.ItemDto;

import java.util.List;

/**
 * Интерфейс, описывающий основные методы сервиса вещей
 */
public interface ItemService {
    /**
     * Метод предназначен для создания вещи
     * @param itemDto DTO создаваемой вещи
     * @param ownerId id пользователя, который создает вещь
     * @return DTO созданной вещи
     */
    ItemDto createItem(ItemDto itemDto, long ownerId);

    /**
     * Метод предназначен для получения вещи по ее id
     * @param itemId id вещи
     * @param userIdRequestFrom id пользователя, который выполняет запрос
     * @return вещь по ее id
     */
    ItemDto getItemDtoById(long itemId, long userIdRequestFrom);

    /**
     * Получение списка всех вещей
     * @return список всех вещей
     */
    List<ItemDto> getAll();

    /**
     * Обновление уже созданной вещи
     * @param itemId id обновляемой вещи
     * @param userIdRequestFrom id пользователя, который выполняет запрос
     * @param itemDto DTO обновленной вещи
     * @return обновленный DTO вещи
     */
    ItemDto updateItem(long itemId, long userIdRequestFrom, ItemDto itemDto);

    /**
     * Получение списка всех вещей пользователя по его id
     * @param ownerId пользователь, чьи вещи запрашиваются
     * @return список вещей этого пользователя
     */
    List<ItemDto> getItemsByOwnerId(long ownerId);

    /**
     * Поиск вещей по описанию
     * @param text текст, содержащий описание
     * @return списо подходящих вещей
     */
    List<ItemDto> findItemsBy(String text);
}
