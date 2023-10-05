package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDtoAfterApproving;
import ru.practicum.shareit.booking.model.BookingDtoAfterCreate;
import ru.practicum.shareit.booking.model.BookingDtoOnCreate;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemInfoDto;
import ru.practicum.shareit.item.service.ItemDtoMapper;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class BookingDtoMapper {
    private final ModelMapper mapper;
    private final ItemDtoMapper itemDtoMapper;

    public Booking fromDtoOnCreate(BookingDtoOnCreate bookingDtoOnCreate) {
        Booking booking = mapper.map(bookingDtoOnCreate, Booking.class);
        long itemId = bookingDtoOnCreate.getItemId();

        booking.getItem().setId(itemId);
        booking.setId(null);
        log.info("Booking после маппинга: {}", booking);
        return booking;
    }

    public BookingDtoOnCreate toDtoOnCreate(Booking booking) {
        BookingDtoOnCreate bookingDtoOnCreate = mapper.map(booking, BookingDtoOnCreate.class);

        bookingDtoOnCreate.setItemId(booking.getItem().getId());
        return bookingDtoOnCreate;
    }

    public BookingDtoAfterCreate toDtoAfterCreate(Booking booking) {
        return mapper.map(booking, BookingDtoAfterCreate.class);
    }

    public BookingDtoAfterApproving toDtoAfterApproving(Booking booking) {
        BookingDtoAfterApproving bookingDtoAfterApproving = mapper.map(booking, BookingDtoAfterApproving.class);
        Item item = booking.getItem();

        bookingDtoAfterApproving.setItem(itemDtoMapper.toItemForBookingDto(item));
        return bookingDtoAfterApproving;
    }

    public List<BookingDtoAfterCreate> toDtoAfterCreate(List<Booking> bookingList) {
        return bookingList.stream()
                .map(this::toDtoAfterCreate)
                .collect(Collectors.toList());
    }

    public ItemInfoDto.BookingDto toDtoForItemInfo(Booking booking) {
        log.info("Маппинг Booking -> toDtoForItemInfo: {}", booking);
        if (booking == null) return null;
        return mapper.map(booking, ItemInfoDto.BookingDto.class);
    }
}
