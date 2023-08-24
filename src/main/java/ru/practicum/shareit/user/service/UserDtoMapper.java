package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

/**
 * Класс предназначен для маппинга объектов пользователя
 */
@Component
@RequiredArgsConstructor
public class UserDtoMapper {
    /**
     * Маппер
     */
    private final ModelMapper mapper;

    /**
     * Метод предназначен для маппинга User -> UserDTO
     *
     * @param user объект пользователя
     * @return DTO объект пользователя
     */
    public UserDto toDto(User user) {
        return mapper.map(user, UserDto.class);
    }

    /**
     * Метод предназначен для маппинга UserDto -> User без создания нового объекта пользователя
     *
     * @param userDto DTO пользователя
     * @param user    уже созданный объект пользователя
     */
    public void fromDto(UserDto userDto, User user) {
        mapper.map(userDto, user);
    }

    /**
     * Метод предназначен для маппинга UserDto -> User
     *
     * @param userDto DTO объект пользователя
     * @return объект пользователя
     */
    public User fromDto(UserDto userDto) {
        return mapper.map(userDto, User.class);
    }
}
