package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.utilities.models.ModelRepository;

import java.util.List;

/**
 * Интерфейс, описывающий методы репозитория вещей.
 */
public interface ItemRepository extends ModelRepository<Item> {
    /**
     * Получение всех вещей пользователя по id пользователя.
     *
     * @param userId id пользователя, вещи которого будут получены.
     * @return список вещей, добавленных пользователем.
     */
    List<Item> getUserItemsByUserId(long userId);

    /**
     * Поиск всех вещей, в описании или в названии которых содержится текст.
     *
     * @param someText текст, по которому будет поиск.
     * @return список подходящих вещей.
     */
    List<Item> findItemsBy(String someText);
}
