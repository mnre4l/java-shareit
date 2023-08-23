package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.utilities.models.Marker;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * Класс-контроллер, обслуживающий исключения
 */
@RestController
@RequestMapping("/items")
@Slf4j
@Validated
@RequiredArgsConstructor
public class ItemController {
    /**
     * Сервис вещей
     */
    private final ItemService itemService;

    /**
     * Добавление вещи в репозиторий
     * @param ownerId пользователь-владелец вещи (id)
     * @param itemDto добавляемая вещь
     * @return созданная вещь
     */
    @PostMapping
    @Validated({Marker.OnCreate.class})
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                           @RequestBody @Valid ItemDto itemDto) {
        log.info("POST /items: получен для ownerId = {}, itemDto = {}", ownerId, itemDto);
        return itemService.createItem(itemDto, ownerId);
    }

    /**
     * Обновление ранее созданной вещи
     * @param userIdRequestFrom id пользователя, который отправил запрос на обновление вещи
     * @param itemId id вещи
     * @param itemDto обновленная вещь
     * @return обновленная вещь
     */
    @PatchMapping("/{itemId}")
    @Validated(Marker.OnUpdate.class)
    @ResponseStatus(HttpStatus.OK)
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userIdRequestFrom,
                              @PathVariable("itemId") @NotNull Long itemId,
                              @RequestBody @Valid ItemDto itemDto) {
        log.info("PATCH /items/itemId: получен для userId = {}, item = {}", userIdRequestFrom, itemDto);
        return itemService.updateItem(itemId, userIdRequestFrom, itemDto);
    }

    /**
     * Получение вещи по id
     * @param userIdRequestFrom id пользователя, который отправил запрос на получение вещи
     * @param itemId id требуемой вещи
     * @return вещь, найденная по ее id
     */
    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") long userIdRequestFrom,
                           @PathVariable("itemId") @NotNull Long itemId) {
        log.info("GET /items/itemId: получен для userId = {}, itemId = {}", userIdRequestFrom, itemId);
        return itemService.getItemDtoById(itemId, userIdRequestFrom);
    }

    /**
     * Получение списка всех вещей пользователя
     * @param userIdRequestFrom id пользователя, от которого пришел запрос
     * @return список вещей пользователя
     */
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") long userIdRequestFrom) {
        log.info("GET /items получен для userId = " + userIdRequestFrom);
        return itemService.getItemsByOwnerId(userIdRequestFrom);
    }

    /**
     * Получение списка всех вещей, удовлетворяющих поиску
     * @param text текст, по которому осуществляется поиск
     * @return список подходящих вещей
     */
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> findItemsByText(@RequestParam("text") String text) {
        log.info("GET /items/search получен для text = " + text);
        return itemService.findItemsBy(text);
    }

}
