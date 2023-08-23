package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.UserDto;

import java.util.List;

/**
 * Интерфейс, описывающие основные методы сервиса пользователей
 */
public interface UserService {
    /**
     * Получение списка всех пользователей
     * @return список всех пользователей
     */
    List<UserDto> getAll();

    /**
     * Получение DTO пользователя по id
     * @param id id пользователя
     * @return DTO объект пользователя
     */
    UserDto getUserById(long id);

    /**
     * Создание пользователя
     * @param user создаваемый пользователь
     * @return созданный пользователь
     */
    UserDto createUser(UserDto user);

    /**
     * Обновление пользователя
     * @param userId id пользователя, которого необходимо обновить
     * @param userDto DTO обновленного пользователя
     * @return обновленный пользователь
     */
    UserDto updateUser(long userId, UserDto userDto);

    /**
     * Удаление пользователя по id
     * @param userId id пользователя, которого необходимо удалить
     */
    void deleteUser(long userId);
}
