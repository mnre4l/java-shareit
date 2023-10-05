package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.utilities.models.Marker;

import javax.validation.Valid;


/**
 * Класс-контроллер, обслуживающий вещи
 */
@RestController
@RequestMapping("/items")
@Slf4j
@Validated
@RequiredArgsConstructor
public class ItemController {
    /*
    чекстайл не пропускает название с нижними подчеркиваниями и статические поля, оставил так
     */
    private static final String xSharerUserId = "X-Sharer-User-Id";
    /**
     * Сервис вещей
     */
    private final ItemClient itemClient;

    /**
     * Добавление вещи в репозиторий
     *
     * @param ownerId пользователь-владелец вещи (id)
     * @param itemDto добавляемая вещь
     * @return созданная вещь
     */
    @PostMapping
    @Validated(Marker.OnCreate.class)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> addItem(@RequestHeader(xSharerUserId) long ownerId,
                                          @RequestBody @Valid ItemDto itemDto) {
        log.info("POST /items: получен для ownerId = {}, itemDto = {}", ownerId, itemDto);
        return itemClient.createItem(itemDto, ownerId);
    }

    /**
     * Обновление ранее созданной вещи
     *
     * @param userIdRequestFrom id пользователя, который отправил запрос на обновление вещи
     * @param itemId            id вещи
     * @param itemDto           обновленная вещь
     * @return обновленная вещь
     */
    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> updateItem(@RequestHeader(xSharerUserId) long userIdRequestFrom,
                                             @PathVariable("itemId") Long itemId,
                                             @RequestBody ItemDto itemDto) {
        log.info("PATCH /items/itemId: получен для userId = {}, item = {}", userIdRequestFrom, itemDto);
        return itemClient.updateItem(itemId, userIdRequestFrom, itemDto);
    }

    /**
     * Получение вещи по id
     *
     * @param userIdRequestFrom id пользователя, который отправил запрос на получение вещи
     * @param itemId            id требуемой вещи
     * @return вещь, найденная по ее id
     */
    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getItem(@RequestHeader(xSharerUserId) long userIdRequestFrom,
                                          @PathVariable("itemId") Long itemId) {
        log.info("GET /items/itemId: получен для userId = {}, itemId = {}", userIdRequestFrom, itemId);
        return itemClient.getItemDtoById(itemId, userIdRequestFrom);
    }

    /**
     * Получение списка всех вещей пользователя
     *
     * @param userIdRequestFrom id пользователя, от которого пришел запрос
     * @return список вещей пользователя
     */
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getUserItems(@RequestHeader(xSharerUserId) long userIdRequestFrom) {
        log.info("GET /items получен для userId = " + userIdRequestFrom);
        return itemClient.getItemsByOwnerId(userIdRequestFrom);
    }

    /**
     * Получение списка всех вещей, удовлетворяющих поиску
     *
     * @param text текст, по которому осуществляется поиск
     * @return список подходящих вещей
     */
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> findItemsByText(@RequestParam("text") String text,
                                                  @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                  @RequestParam(value = "size", defaultValue = "20") Integer size) {
        log.info("GET /items/search получен для text = " + text);
        return itemClient.findItemsBy(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(xSharerUserId) long userIdRequestFrom,
                                             @PathVariable("itemId") Long itemId,
                                             @RequestBody @Valid CommentDto commentDto) {
        log.info("POST /items/itemId/comment получен для itemId = {}", itemId);
        log.info("text: {}", commentDto.getText());
        return itemClient.addComment(userIdRequestFrom, itemId, commentDto);
    }

}
