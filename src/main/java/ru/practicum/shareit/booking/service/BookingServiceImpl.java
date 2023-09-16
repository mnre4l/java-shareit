package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserBookingStates;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingDtoMapper mapper;
    private final BookingRepository bookingRepository;

    @Override
    public BookingDtoAfterCreate createBooking(BookingDtoOnCreate bookingDto, Long bookerId) {
        User booker = userService.getUserById(bookerId);
        Booking booking = mapper.fromDtoOnCreate(bookingDto);
        long itemId = booking.getItem().getId();
        Item item = itemService.getItemById(itemId);

        itemService.checkItemIsAvailable(item);
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
    public BookingDtoAfterApproving confirmBooking(Long bookingId, Long ownerId, Boolean isApproved) {
        Booking booking = bookingRepository.findById(bookingId).
                orElseThrow(() -> new NotFoundException("Не найдена бронь с id = " + bookingId));

        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new StatusCanNotBeChangedException("Бронь id = " + booking.getId() + "уже была рассмотрена владельцем");
        }
        /*
        проверка что пользователь существует
         */
        User owner = userService.getUserById(ownerId);

        itemService.checkIsUserItemOwner(booking.getItem(), owner.getId());
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
    public List<BookingDtoAfterCreate> getUserBookings(Long userId, UserBookingStates state) {
        User booker = userService.getUserById(userId);
        /**
         * передалть в протекц интерфейс маппинг
         */
        switch (state) {
            case ALL: {
                List<Booking> bookingList = bookingRepository
                        .findAllByBooker_IdOrderByStartDesc(booker.getId());
                return mapper.toDtoAfterCreate(bookingList);
            }
            case FUTURE: {
                List<Booking> bookingList = bookingRepository
                        .findAllByBooker_IdAndStartAfterOrderByStartDesc(booker.getId(), LocalDateTime.now());
                return mapper.toDtoAfterCreate(bookingList);
            }
            case PAST: {
                List<Booking> bookingList = bookingRepository
                        .findAllByBooker_IdAndEndBeforeOrderByStartDesc(booker.getId(), LocalDateTime.now());
                return mapper.toDtoAfterCreate(bookingList);
            }
            case CURRENT: {
                List<Booking> bookingList = bookingRepository
                        .findAllByBooker_IdAndStartAfterAndEndBeforeOrderByStartDesc(booker.getId(),
                                LocalDateTime.now(), LocalDateTime.now());
                return mapper.toDtoAfterCreate(bookingList);
            }
            default: {
                String status = state.name();
                List<Booking> bookingList = bookingRepository
                        .findAllByBooker_IdAndStatus(booker.getId(), Status.valueOf(status));

                return mapper.toDtoAfterCreate(bookingList);
            }
        }
    }

    @Override
    public List<BookingDtoAfterCreate> getBookingsByOwner(Long ownerId, UserBookingStates state) {
        User owner = userService.getUserById(ownerId);
        /**
         * передалть в протекц интерфейс маппинг
         */
        switch (state) {
            case ALL: {
                List<Booking> bookingList = bookingRepository
                        .findAllByItem_Owner_IdOrderByStartDesc(owner.getId());
                return mapper.toDtoAfterCreate(bookingList);
            }
            case FUTURE: {
                List<Booking> bookingList = bookingRepository
                        .findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(owner.getId(), LocalDateTime.now());
                return mapper.toDtoAfterCreate(bookingList);
            }
            case PAST: {
                List<Booking> bookingList = bookingRepository
                        .findAllByItem_Owner_IdAndEndBeforeOrderByStartDesc(owner.getId(), LocalDateTime.now());
                return mapper.toDtoAfterCreate(bookingList);
            }
            case CURRENT: {
                List<Booking> bookingList = bookingRepository
                        .findAllByItem_Owner_IdAndStartAfterAndEndBeforeOrderByStartDesc(owner.getId(),
                                LocalDateTime.now(), LocalDateTime.now());
                return mapper.toDtoAfterCreate(bookingList);
            }
            default: {
                String status = state.name();
                List<Booking> bookingList = bookingRepository
                        .findAllByItem_Owner_IdAndStatus(owner.getId(), Status.valueOf(status));

                return mapper.toDtoAfterCreate(bookingList);
            }
        }
    }


    private void checkBookingAccessFor(Long userRequestFromId, Booking booking) {
        Long bookerId = booking.getBooker().getId();
        Long itemOwnerId = booking.getItem().
                getOwner().
                getId();
        if (!(userRequestFromId.equals(bookerId) || userRequestFromId.equals(itemOwnerId))) {
            throw new AccessDeniedException("Доступ к брони id = " + booking.getId() + " запрещен для " +
                    "пользователя id = " + userRequestFromId);
        }
    }

    private void checkItemCanBeBookedForTime(Item item, LocalDateTime start, LocalDateTime end) {
        /*
        получаем все подтвержденные брони для интересующей вещи
         */
        List<Booking> itemBookings = bookingRepository.findAllByItem_IdAndStatus(item.getId(), Status.APPROVED);

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

}
