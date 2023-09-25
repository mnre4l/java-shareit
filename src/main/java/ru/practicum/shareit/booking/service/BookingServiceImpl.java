package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserBookingStates;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utilities.models.Page;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingDtoMapper mapper;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public BookingDtoAfterCreate createBooking(BookingDtoOnCreate bookingDto, Long bookerId) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id = " + bookerId));
        Booking booking = mapper.fromDtoOnCreate(bookingDto);
        long itemId = booking.getItem().getId();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Не найден Item с id = " + bookerId));

        checkItemIsAvailable(item);
        checkItemCanBeBookedForTime(item, booking.getStart(), booking.getEnd());
        checkItemCanBeBookedByUser(item, booker);

        booking.setStatus(Status.WAITING);
        booking.setItem(item);
        booking.setBooker(booker);
        log.info("Сохраняем бронь: {}", booking);

        Booking savedBooking = bookingRepository.save(booking);

        log.info("Сохранена бронь: {}", savedBooking);
        return mapper.toDtoAfterCreate(savedBooking);
    }

    @Override
    public BookingDtoAfterApproving confirmBooking(Long bookingId, Long shouldBeOwnerId, Boolean isApproved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Не найдена бронь с id = " + bookingId));
        /*
        проверка что пользователь существует
         */
        User owner = userRepository.findById(shouldBeOwnerId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id = " + shouldBeOwnerId));

        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new StatusCanNotBeChangedException("Бронь id = " + booking.getId() + "уже была рассмотрена владельцем");
        }

        Long ownerId = booking.getItem()
                .getOwner()
                .getId();

        if (!shouldBeOwnerId.equals(ownerId))
            throw new UserIsNotItemOwnerException(String.format("Пользователь с id = %s не является " +
                    "владельцем вещи %s. Ее владелец пользователь id = %s", shouldBeOwnerId, booking.getItem(), ownerId));
        if (isApproved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        Booking savedBooking = bookingRepository.save(booking);

        return mapper.toDtoAfterApproving(savedBooking);
    }

    @Override
    public BookingDtoAfterApproving getBookingById(Long bookingId, Long userRequestFrom) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Не найдена бронь с id = " + bookingId));

        checkBookingAccessFor(userRequestFrom, booking);
        return mapper.toDtoAfterApproving(booking);
    }

    @Override
    public List<BookingDtoAfterCreate> getUserBookings(Long userId, UserBookingStates state, Integer from, Integer size) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id = " + userId));
        Page page = new Page(from, size, Sort.unsorted());

        switch (state) {
            case ALL: {
                List<Booking> bookingList = bookingRepository
                        .findAllByBooker_IdOrderByStartDesc(booker.getId(), page);
                return mapper.toDtoAfterCreate(bookingList);
            }
            case FUTURE: {
                List<Booking> bookingList = bookingRepository
                        .findAllByBooker_IdAndStartAfterOrderByStartDesc(booker.getId(), LocalDateTime.now(), page);
                return mapper.toDtoAfterCreate(bookingList);
            }
            case PAST: {
                List<Booking> bookingList = bookingRepository
                        .findAllByBooker_IdAndEndBeforeOrderByStartDesc(booker.getId(), LocalDateTime.now(), page);
                return mapper.toDtoAfterCreate(bookingList);
            }
            case CURRENT: {
                List<Booking> bookingList = bookingRepository
                        .findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(booker.getId(),
                                LocalDateTime.now(), LocalDateTime.now(), page);
                return mapper.toDtoAfterCreate(bookingList);
            }
            default: {
                String status = state.name();
                List<Booking> bookingList = bookingRepository
                        .findAllByBooker_IdAndStatus(booker.getId(), Status.valueOf(status), page);

                return mapper.toDtoAfterCreate(bookingList);
            }
        }
    }

    @Override
    public List<BookingDtoAfterCreate> getBookingsByOwner(Long ownerId, UserBookingStates state, Integer from, Integer size) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id = " + ownerId));
        Page page = new Page(from, size, Sort.unsorted());

        switch (state) {
            case ALL: {
                List<Booking> bookingList = bookingRepository
                        .findAllByItem_Owner_IdOrderByStartDesc(owner.getId(), page);
                return mapper.toDtoAfterCreate(bookingList);
            }
            case FUTURE: {
                List<Booking> bookingList = bookingRepository
                        .findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(owner.getId(), LocalDateTime.now(), page);
                return mapper.toDtoAfterCreate(bookingList);
            }
            case PAST: {
                List<Booking> bookingList = bookingRepository
                        .findAllByItem_Owner_IdAndEndBeforeOrderByStartDesc(owner.getId(), LocalDateTime.now(), page);
                return mapper.toDtoAfterCreate(bookingList);
            }
            case CURRENT: {
                List<Booking> bookingList = bookingRepository
                        .findAllByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(owner.getId(),
                                LocalDateTime.now(), LocalDateTime.now(), page);
                return mapper.toDtoAfterCreate(bookingList);
            }
            default: {
                String status = state.name();
                List<Booking> bookingList = bookingRepository
                        .findAllByItem_Owner_IdAndStatus(owner.getId(), Status.valueOf(status), page);

                return mapper.toDtoAfterCreate(bookingList);
            }
        }
    }

    private void checkBookingAccessFor(Long userRequestFromId, Booking booking) {
        Long bookerId = booking.getBooker().getId();
        Long itemOwnerId = booking.getItem()
                .getOwner()
                .getId();
        if (!(userRequestFromId.equals(bookerId) || userRequestFromId.equals(itemOwnerId))) {
            throw new AccessDeniedException("Доступ к брони id = " + booking.getId() + " запрещен для " +
                    "пользователя id = " + userRequestFromId);
        }
    }

    private void checkItemCanBeBookedForTime(Item item, LocalDateTime start, LocalDateTime end) {
        /*
        получаем все подтвержденные брони для интересующей вещи
         */
        List<Booking> itemBookings = bookingRepository.findAllByItem_IdAndStatus(item.getId(), Status.APPROVED, null);

        for (Booking booking : itemBookings) {
            LocalDateTime notAvailableStart = booking.getStart();
            LocalDateTime notAvailableEnd = booking.getEnd();

            if ((start.isAfter(notAvailableStart)) && (start.isBefore(notAvailableEnd)) ||
                    (end.isAfter(notAvailableStart)) && (end.isBefore(notAvailableEnd))) {
                throw new ItemNotAvailableException("Вещь забронирована на период: " + start + "-" + end);
            }
        }
    }

    private void checkItemCanBeBookedByUser(Item item, User booker) {
        Long ownerId = item.getOwner().getId();
        Long bookerId = booker.getId();

        if (ownerId.equals(bookerId)) {
            throw new UserTryBookingItsItemException("Пользователь не может бронировать свою вещь");
        }
    }

    private void checkItemIsAvailable(Item item) {
        if (!item.getAvailable()) {
            throw new ItemNotAvailableException("Item id = " + item.getId() + "недоступно для бронирования");
        }
    }

}
