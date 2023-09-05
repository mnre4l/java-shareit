package ru.practicum.shareit.utilities.models;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс, описывающий CRUD операции репозитория, типичные для всех моделей.
 *
 * @param <T> тип объекта, который хранится в репозитории.
 */
public interface ModelRepository<T> {
    /**
     * Создание сущности
     *
     * @param t сохраняемая сущность
     */
    void create(T t);

    /**
     * Обновление уже существующей сущности
     *
     * @param t обновляемая сущность.
     */
    void update(T t);

    /**
     * Удаление сущности
     *
     * @param t удаляемая сущность.
     */
    void delete(T t);

    /**
     * Получение сущности по id.
     *
     * @param id id сущности.
     * @return Optional-обретка запрашиваемой сущности.
     */
    Optional<T> get(Long id);

    /**
     * Получение всех созданных сущностей.
     *
     * @return список всех созданных сущностей или null.
     */
    List<T> getAll();

    /**
     * Удаление всех хранимых сущностей.
     */
    void deleteAll();
}

