package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.user.model.UserBookingStates;
import ru.practicum.shareit.utilities.models.Marker;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;


/**
 * Класс-контроллер, обслуживающий бронирование вещей
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/bookings")
public class BookingController {
    /**
     * Сервис бронирования
     */
    private final BookingService bookingService;

    /**
     * Создание брони
     * @param bookerId пользователь, который бронирует вещь
     * @param bookingDto бронируемая вещь
     * @return забронированная вещь
     */
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @Validated({Marker.OnCreate.class})
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") @NotNull final Long bookerId,
                                    @RequestBody @Valid final BookingDto bookingDto) {
        return null;
    }

    /**
     * Подтверждение бронирование
     * @param bookingId id уже созданной брони
     * @param ownerId id владельца вещи
     * @param approved подтверждение/опровержение бронирования от владельца
     */
    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public void confirmBooking(@PathVariable final Long bookingId,
                                 @RequestHeader("X-Sharer-User-Id") @NotNull final Long ownerId,
                                 @RequestParam @NotNull final Boolean approved) {
    }

    /**
     * Получение брони по id
     * @param userRequestFromId id пользователя, от которого запрос (или владелец, или бронирующий пользователь)
     * @param bookingId id брони
     * @return DTO брони
     */
    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") @NotNull final Long userRequestFromId,
                                 @PathVariable @NotNull final Long bookingId) {
        return null;
    }

    /**
     * Получение списка броней, созданных пользователем
     * @param userId id пользователя, чьи брони необходимо вернуть
     * @param bookingsState выборка статуса брони
     * @return список подходящих по статусу броней
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getUserBookings(@RequestHeader("X-Sharer-User-Id") @NotNull final Long userId,
                                            @PathVariable(value = "state") Optional<UserBookingStates> bookingsState) {
        UserBookingStates state = bookingsState.orElse(UserBookingStates.ALL);
        return null;
    }

    /**
     * Получение списка броней для вещей, которые принадлежат пользователю
     * @param ownerId id владельца вещей
     * @param bookingsState выборка статуса вещей
     * @return список подходящих по статусу вещей
     */
    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") @NotNull final Long ownerId,
                                               @PathVariable(value = "state") Optional<UserBookingStates> bookingsState) {
        UserBookingStates state = bookingsState.orElse(UserBookingStates.ALL);
        return null;
    }
}
