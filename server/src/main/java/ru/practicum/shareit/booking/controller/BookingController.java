package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.BookingDtoAfterApproving;
import ru.practicum.shareit.booking.model.BookingDtoAfterCreate;
import ru.practicum.shareit.booking.model.BookingDtoOnCreate;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.user.model.UserBookingStates;

import java.util.List;


/**
 * Класс-контроллер, обслуживающий бронирование вещей
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/bookings")
public class BookingController {
    /*
    чекстайл не пропускает нижние подчеркивания и статические поля, оставил так
     */
    private final String xSharerUserId = "X-Sharer-User-Id";
    /**
     * Сервис бронирования
     */
    private final BookingService bookingService;

    /**
     * Создание брони
     *
     * @param bookerId           пользователь, который бронирует вещь
     * @param bookingDtoOnCreate бронируемая вещь
     * @return забронированная вещь
     */
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public BookingDtoAfterCreate createBooking(@RequestHeader(xSharerUserId) final Long bookerId,
                                               @RequestBody final BookingDtoOnCreate bookingDtoOnCreate) {
        log.info("POST /bookings получен для: {}, от пользователя id = {}", bookingDtoOnCreate, bookerId);
        return bookingService.createBooking(bookingDtoOnCreate, bookerId);
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
    public BookingDtoAfterApproving confirmBooking(@PathVariable final Long bookingId,
                                                   @RequestHeader(xSharerUserId) final Long ownerId,
                                                   @RequestParam final Boolean approved) {
        log.info("PATCH /bookings/bookingId получен для: bookingId = {}, от пользователя id = {}", bookingId, ownerId);
        return bookingService.confirmBooking(bookingId, ownerId, approved);
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
    public BookingDtoAfterApproving getBooking(@RequestHeader(xSharerUserId) final Long userRequestFromId,
                                               @PathVariable final Long bookingId) {
        log.info("GET /bookings/bookingId получен для: bookingId = {}, от пользователя id = {}", bookingId, userRequestFromId);
        return bookingService.getBookingById(bookingId, userRequestFromId);
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
    public List<BookingDtoAfterCreate> getUserBookings(@RequestHeader(xSharerUserId) final Long userId,
                                                       @RequestParam(value = "state", defaultValue = "ALL") String bookingsState,
                                                       @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                       @RequestParam(value = "size", defaultValue = "20") Integer size) {
        return bookingService.getUserBookings(userId, UserBookingStates.valueOf(bookingsState), from, size);
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
    public List<BookingDtoAfterCreate> getBookingsByOwner(@RequestHeader(xSharerUserId) final Long ownerId,
                                                          @RequestParam(value = "state", defaultValue = "ALL") String bookingsState,
                                                          @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                          @RequestParam(value = "size", defaultValue = "20") Integer size) {
        return bookingService.getBookingsByOwner(ownerId, UserBookingStates.valueOf(bookingsState), from, size);
    }
}
