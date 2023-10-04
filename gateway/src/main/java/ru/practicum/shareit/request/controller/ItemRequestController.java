package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.model.ItemRequestDtoOnCreate;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


/**
 * Класс-контроллер, обслуживающий запросы на вещи
 */
@RestController
@RequestMapping(path = "/requests")
@Slf4j
@Validated
@RequiredArgsConstructor
public class ItemRequestController {
    private static final String xSharerUserId = "X-Sharer-User-Id";
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createRequest(@RequestHeader(xSharerUserId) Long userId,
                                                @RequestBody @Valid ItemRequestDtoOnCreate request) {
        log.info("POST /requests: получен для userId = {}, ItemRequestDtoOnCreate = {}", userId, request);
        return itemRequestClient.createRequest(userId, request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getRequestsByOwner(@RequestHeader(xSharerUserId) Long ownerId) {
        log.info("GET /requests: получен для ownerId = {}", ownerId);
        return itemRequestClient.getRequestsByOwner(ownerId);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getRequestsById(@PathVariable(value = "requestId") Long requestId,
                                                  @RequestHeader(xSharerUserId) Long userRequestFromId) {
        log.info("GET /requests/{requestId}: получен для requestId = {} от пользователя id = {}", requestId, userRequestFromId);
        return itemRequestClient.getRequestById(requestId, userRequestFromId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllRequests(@RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                 @RequestParam(value = "size", defaultValue = "20") @Positive Integer size,
                                                 @RequestHeader(xSharerUserId) Long userRequestFromId) {
        log.info("GET /requests/all: получен для from = {}, size ={} от пользователя id =", from, size, userRequestFromId);
        return itemRequestClient.getAllRequests(from, size, userRequestFromId);
    }
}

