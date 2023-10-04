package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingDtoOnCreate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exception.model.BadBookingStatusException;
import ru.practicum.shareit.user.model.UserBookingStates;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


/**
 * Класс-контроллер, обслуживающий бронирование вещей
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String X_SHARER_USER_ID = BaseClient.X_SHARER_USER_ID;
    /*
     * Сервис бронирования
     */
    private final BookingClient bookingClient;

    /**
     * Создание брони
     *
     * @param bookerId           пользователь, который бронирует вещь
     * @param bookingDtoOnCreate бронируемая вещь
     * @return забронированная вещь
     */
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @Validated
    public ResponseEntity<Object> createBooking(@RequestHeader(X_SHARER_USER_ID) @NotNull final Long bookerId,
                                                @RequestBody @Valid final BookingDtoOnCreate bookingDtoOnCreate) {
        log.info("POST /bookings получен для: {}, от пользователя id = {}", bookingDtoOnCreate, bookerId);
        return bookingClient.createBooking(bookerId, bookingDtoOnCreate);
    }

    /**
     * Подтверждение бронирование
     *
     * @param bookingId id уже созданной брони
     * @param ownerId   id владельца вещи
     * @param approved  подтверждение/опровержение бронирования от владельца
     */
    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> confirmBooking(@PathVariable final Long bookingId,
                                                 @RequestHeader(X_SHARER_USER_ID) @NotNull final Long ownerId,
                                                 @RequestParam @NotNull final Boolean approved) {
        log.info("PATCH /bookings/bookingId получен для: bookingId = {}, от пользователя id = {}", bookingId, ownerId);
        return bookingClient.confirmBooking(bookingId, ownerId, approved);
    }

    /**
     * Получение брони по id
     *
     * @param userRequestFromId id пользователя, от которого запрос (или владелец, или бронирующий пользователь)
     * @param bookingId         id брони
     * @return DTO брони
     */
    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getBooking(@RequestHeader(X_SHARER_USER_ID) @NotNull final Long userRequestFromId,
                                             @PathVariable @NotNull final Long bookingId) {
        log.info("GET /bookings/bookingId получен для: bookingId = {}, от пользователя id = {}", bookingId, userRequestFromId);
        return bookingClient.getBookingById(bookingId, userRequestFromId);
    }

    /**
     * Получение списка броней, созданных пользователем
     *
     * @param userId        id пользователя, чьи брони необходимо вернуть
     * @param bookingsState выборка статуса брони
     * @return список подходящих по статусу броней
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getUserBookings(@RequestHeader(X_SHARER_USER_ID) @NotNull final Long userId,
                                                  @RequestParam(value = "state", defaultValue = "ALL") String bookingsState,
                                                  @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                  @RequestParam(value = "size", defaultValue = "20") Integer size) {
        UserBookingStates state = UserBookingStates.from(bookingsState)
                .orElseThrow(() -> new BadBookingStatusException("Unknown state: " + bookingsState));
        return bookingClient.getUserBookings(userId, state, from, size);
    }

    /**
     * Получение списка броней для вещей, которые принадлежат пользователю
     *
     * @param ownerId       id владельца вещей
     * @param bookingsState выборка статуса вещей
     * @return список подходящих по статусу вещей
     */
    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getBookingsByOwner(@RequestHeader(X_SHARER_USER_ID) @NotNull final Long ownerId,
                                                     @RequestParam(value = "state", defaultValue = "ALL") String bookingsState,
                                                     @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                     @RequestParam(value = "size", defaultValue = "20") Integer size) {
        UserBookingStates state = UserBookingStates.from(bookingsState)
                .orElseThrow(() -> new BadBookingStatusException("Unknown state: " + bookingsState));
        return bookingClient.getBookingsByOwner(ownerId, state, from, size);
    }
}
