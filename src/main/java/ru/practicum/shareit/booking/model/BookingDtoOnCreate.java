package ru.practicum.shareit.booking.model;

import lombok.Data;
import ru.practicum.shareit.booking.service.StartBeforeEnd;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Data
@StartBeforeEnd
public class BookingDtoOnCreate {
    @NotNull
    private Long itemId;
    @FutureOrPresent
    @NotNull
    private LocalDateTime start;
    @Future
    @NotNull
    private LocalDateTime end;

}
