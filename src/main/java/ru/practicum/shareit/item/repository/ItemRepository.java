package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

/**
 * Интерфейс, описывающий методы репозитория вещей.
 */
public interface ItemRepository extends JpaRepository<Item, Long> {
    /**
     * Получение всех вещей пользователя по id пользователя.
     *
     * @param userId id пользователя, вещи которого будут получены.
     * @return список вещей, добавленных пользователем.
     */
    List<Item> findByOwner_IdOrderByIdAsc(long userId);

    /**
     * Поиск всех вещей, в описании или в названии которых содержится текст.
     *
     * @param someText текст, по которому будет поиск.
     * @return список подходящих вещей.
     */
    List<Item> findByAvailableTrueAndDescriptionContainingIgnoreCase(String someText);
}
