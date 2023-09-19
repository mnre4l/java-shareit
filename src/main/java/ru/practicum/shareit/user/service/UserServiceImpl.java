package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис пользователей
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    /**
     * Репозиторий пользователей
     */
    private final UserRepository userRepository;
    /**
     * Маппер пользователей
     */
    private final UserDtoMapper mapper;

    @Override
    public List<UserDto> getAll() {
        List<UserDto> allUsers = userRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());

        log.info("Возвращен список пользователей: {}", allUsers);
        return allUsers;
    }

    @Override
    public UserDto getUserDtoById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id = " + id));

        log.info("Возвращен пользователь: {}", user);
        return mapper.toDto(user);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = mapper.fromDto(userDto);

        userRepository.save(user);
        log.info("Создан пользователь: {}", user);
        return mapper.toDto(user);
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDtoUpdated) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id = " + userId));

        mapper.fromDto(userDtoUpdated, user);
        userRepository.save(user);
        log.info("Обновлен пользователь: {}", user);
        return mapper.toDto(user);
    }

    @Override
    public void deleteUser(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id = " + userId));

        userRepository.delete(user);
        log.info("Удален пользователь: {}", user);
    }

    @Override
    public User getUserById(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id = " + userId));

        return user;
    }

}
