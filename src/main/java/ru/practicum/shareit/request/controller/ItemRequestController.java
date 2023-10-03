package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.model.ItemRequestDtoAfterCreate;
import ru.practicum.shareit.request.model.ItemRequestDtoInfo;
import ru.practicum.shareit.request.model.ItemRequestDtoOnCreate;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.utilities.models.Marker;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


/**
 * Класс-контроллер, обслуживающий вещи
 */
@RestController
@RequestMapping(path = "/requests")
@Slf4j
@Validated
@RequiredArgsConstructor
public class ItemRequestController {
    private static final String xSharerUserId = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    @Validated({Marker.OnCreate.class})
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDtoAfterCreate createRequest(@RequestHeader(xSharerUserId) Long userId,
                                                   @RequestBody @Valid ItemRequestDtoOnCreate request) {
        log.info("POST /requests: получен для userId = {}, ItemRequestDtoOnCreate = {}", userId, request);
        return itemRequestService.createRequest(userId, request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDtoInfo> getRequestsByOwner(@RequestHeader(xSharerUserId) Long ownerId) {
        log.info("GET /requests: получен для ownerId = {}", ownerId);
        return itemRequestService.getRequestsByOwner(ownerId);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDtoInfo getRequestsById(@PathVariable(value = "requestId") Long requestId,
                                              @RequestHeader(xSharerUserId) Long userRequestFromId) {
        log.info("GET /requests/{requestId}: получен для requestId = {} от пользователя id = {}", requestId, userRequestFromId);
        return itemRequestService.getRequestById(requestId, userRequestFromId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDtoInfo> getAllRequests(@RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                   @RequestParam(value = "size", defaultValue = "20") @Positive Integer size,
                                                   @RequestHeader(xSharerUserId) Long userRequestFromId) {
        log.info("GET /requests/all: получен для from = {}, size ={} от пользователя id =", from, size, userRequestFromId);
        return itemRequestService.getAllRequests(from, size, userRequestFromId);
    }
}

