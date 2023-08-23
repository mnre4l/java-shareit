package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utilities.models.Marker;

import javax.validation.Valid;
import java.util.List;

/**
 * Класс-контроллер, обслуживающий пользователей.
 */
@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
public class UserController {
    /**
     * Сервис пользователей
     */
    private final UserService userService;

    /**
     * Эндпоинт GET /users.
     *
     * @return список пользователей.
     */
    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getUsers() {
        log.info("GET /users/");
        return userService.getAll();
    }

    /**
     * Эндпоинт GET /users/{id}
     *
     * @param userId id заправшиваемого пользователя.
     * @return объект запрашиваемого пользователя.
     */
    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUser(@PathVariable("userId") long userId) {
        log.info("GET /users/{}", userId);
        return userService.getUserById(userId);
    }

    /**
     * Эндпоинт POST /users.
     * Предназнчен для создания пользователя.
     *
     * @param userDto создаваемый пользователь
     * @return объект созданного пользователя в случае успеха
     */
    @PostMapping("/users")
    @Validated({Marker.OnCreate.class})
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        log.info("POST /users: получен для {}", userDto);
        return userService.createUser(userDto);
    }

    /**
     * Эндпоинт /users/userId
     * Предназначен для обновления пользователя
     * @param userDtoUpdated DTO обновленного пользователя
     * @param userId id пользователя, которого нужно обновить
     * @return обновленный пользователь (DTO)
     */
    @PatchMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateUser(@RequestBody UserDto userDtoUpdated, @PathVariable("userId") long userId) {
        log.info("PATCH /users/userId: получен для id = {}", userId);
        return userService.updateUser(userId, userDtoUpdated);
    }

    /**
     * Эндпоинт DELETE users/userId. Предназначен для удаления пользователя
     * @param userId id пользователя, которого необходимо удалить
     */
    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable("userId") long userId) {
        log.info("DELETE /users/userId: получен для id = {}", userId);
        userService.deleteUser(userId);
    }
}
