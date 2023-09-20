package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.BookingDtoOnCreate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEnd, BookingDtoOnCreate> {
    @Override
    public boolean isValid(BookingDtoOnCreate bookingDtoOnCreate, ConstraintValidatorContext constraintValidatorContext) {
        return bookingDtoOnCreate.getStart().isBefore(bookingDtoOnCreate.getEnd());
    }
}
