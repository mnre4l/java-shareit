package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.BookingDtoAfterApproving;
import ru.practicum.shareit.booking.model.BookingDtoAfterCreate;
import ru.practicum.shareit.booking.model.BookingDtoOnCreate;
import ru.practicum.shareit.user.model.UserBookingStates;

import java.util.List;

public interface BookingService {
    BookingDtoAfterCreate createBooking(BookingDtoOnCreate bookingDto, Long bookerId);

    BookingDtoAfterApproving confirmBooking(Long bookingId, Long ownerId, Boolean isApproved);

    BookingDtoAfterApproving getBookingById(Long bookingId, Long userRequestFrom);

    List<BookingDtoAfterCreate> getUserBookings(Long userId, UserBookingStates state);

    List<BookingDtoAfterCreate> getBookingsByOwner(Long ownerId, UserBookingStates state);

}
