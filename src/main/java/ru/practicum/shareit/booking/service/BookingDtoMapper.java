package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDtoAfterApproving;
import ru.practicum.shareit.booking.model.BookingDtoAfterCreate;
import ru.practicum.shareit.booking.model.BookingDtoOnCreate;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class BookingDtoMapper {
    private final ModelMapper mapper;

    public Booking fromDtoOnCreate(BookingDtoOnCreate bookingDtoOnCreate) {
        Booking booking = mapper.map(bookingDtoOnCreate, Booking.class);
        long itemId = bookingDtoOnCreate.getItemId();

        booking.getItem().setId(itemId);
        /*
        почему-то библиотечный маппер устанавливал значения id. пока не разобрался почему,
        оставил явный нул
         */
        booking.setId(null);
        log.info("Booking после маппинга: {}", booking);
        return booking;
    }

    public BookingDtoAfterCreate toDtoAfterCreate(Booking booking) {
        return mapper.map(booking, BookingDtoAfterCreate.class);
    }

    public BookingDtoAfterApproving toDtoAfterApproving(Booking booking) {
        return mapper.map(booking, BookingDtoAfterApproving.class);
    }

    public List<BookingDtoAfterCreate> toDtoAfterCreate(List<Booking> bookingList) {
        return bookingList.stream()
                .map(this::toDtoAfterCreate)
                .collect(Collectors.toList());
    }
}
