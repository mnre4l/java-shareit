package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.ItemNotAvailableException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.UserIsNotItemOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserDtoMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceBase implements ItemService {
    /**
     * Сервис пользоввателей
     */
    private final UserService userService;
    /**
     * Репозиторий вещей
     */
    private final ItemRepository itemRepository;
    /**
     * Маппер вещей
     */
    private final ItemDtoMapper itemMapper;
    /**
     * Маппер пользователей
     */
    private final UserDtoMapper userMapper;

    @Override
    public ItemDto createItem(ItemDto itemDto, long ownerId) {
        UserDto ownerDto = userService.getUserDtoById(ownerId);
        User owner = userMapper.fromDto(ownerDto);
        Item item = itemMapper.fromDto(itemDto, owner);

        itemRepository.save(item);
        log.info("Создана вещь: {}", item);
        return itemMapper.toDto(item);
    }


    @Override
    public ItemDto getItemDtoById(long id, long userIdRequestFrom) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найден item с id = " + id));

        log.info("Возвращен item: {}", item);
        return itemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> getAll() {
        List<ItemDto> items = itemRepository.findAll().stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
        return null;
    }

    @Override
    public ItemDto updateItem(long itemId, long userIdRequestFrom, ItemDto updatedItem) {
        Item itemBeforeUpdate = getItemById(itemId);

        log.info("Найден item для обновления: {}", itemBeforeUpdate);

        checkIsUserItemOwner(itemBeforeUpdate, userIdRequestFrom);
        itemMapper.fromDto(updatedItem, itemBeforeUpdate);
        itemRepository.save(itemBeforeUpdate);
        return getItemDtoById(itemId, userIdRequestFrom);
    }

    @Override
    public List<ItemDto> getItemsByOwnerId(long userId) {
        return itemRepository.findByOwner_Id(userId).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findItemsBy(String text) {
        //наверное, по хорошему нужно делать валидацию на someText, но тесты требуют именно пустой лист
        if (text.isBlank()) return Collections.emptyList();
        return itemRepository.findByAvailableTrueAndDescriptionContainingIgnoreCase(text).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }


    @Override
    public Item getItemById(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Не найден item с id = " + itemId));
    }

    /**
     * Метод предназначен для проверки, является ли пользователем владельцем вещи
     *
     * @param item   объект вещи
     * @param userId преподалагемый владелец вещи (id)
     */
    @Override
    public void checkIsUserItemOwner(Item item, long userId) {
        long ownerId = item.getOwner().getId();

        if (ownerId != userId)
            throw new UserIsNotItemOwnerException(String.format("Пользователь с id = %s не является " +
                    "владельцем вещи %s. Ее владелец пользователь id = %s", userId, item, ownerId));
    }

    @Override
    public void checkItemIsAvailable(Item item) {
        if (!item.getAvailable()) {
            throw new ItemNotAvailableException("Item id = " + item.getId() + "недоступно для бронирования");
        }
    }

}
