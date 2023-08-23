package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.EmailIsAlreadyUsedException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервис пользователей
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceBase implements UserService {
    /**
     * Репозиторий пользователей
     */
    @Qualifier("InMemoryUserRepository")
    private final UserRepository userRepository;
    /**
     * Маппер пользователей
     */
    private final UserDtoMapper mapper;

    @Override
    public List<UserDto> getAll() {
        List<UserDto> allUsers = userRepository.getAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());

        log.info("Возвращен список пользователей: {}", allUsers);
        return allUsers;
    }

    @Override
    public UserDto getUserById(long id) {
        User user = userRepository.get(id)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id = " + id));

        log.info("Возвращен пользователь: {}", user);
        return mapper.toDto(user);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = mapper.fromDto(userDto);

        checkUserEmailIsUnique(Optional.ofNullable(userDto.getId()), user.getEmail());
        userRepository.create(user);
        log.info("Создан пользователь: {}", user);
        return mapper.toDto(user);
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDtoUpdated) {
        checkUserEmailIsUnique(Optional.of(userId), userDtoUpdated.getEmail());

        User user = userRepository.get(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id = " + userId));

        mapper.fromDto(userDtoUpdated, user);
        userRepository.update(user);
        log.info("Обновлен пользователь: {}", user);
        return mapper.toDto(user);
    }

    @Override
    public void deleteUser(long userId) {
        User user = userRepository.get(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id = " + userId));

        userRepository.delete(user);
        log.info("Удален пользователь: {}", user);
    }

    /**
     * Метод предназначен для проверки имейла пользователя
     *
     * @param userId айди проверяемого пользователя
     * @param email  проверяемый имейл
     */
    private void checkUserEmailIsUnique(Optional<Long> userId, String email) {
        log.info("Валидация имейла пользователя id = {}, email = {}", userId, email);
        /*
         id = -1 принимается для создаваемых пользователей
         */
        long id = userId.orElse(-1L);

        List<String> emails = userRepository.getAll().stream()
                .filter(userFromStream -> userFromStream.getId() != id)
                .map(User::getEmail)
                .collect(Collectors.toList());
        if (emails.contains(email)) {
            throw new EmailIsAlreadyUsedException("Пользователь с таким email уже существует: " + email);
        }
    }
}
