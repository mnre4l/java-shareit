package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByItemAndStatus(Item item, Status status, Pageable pageable);

    List<Booking> findAllByBookerOrderByStartDesc(User booker, Pageable pageable);

    List<Booking> findAllByBookerAndStartAfterOrderByStartDesc(User booker, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByBookerAndEndBeforeOrderByStartDesc(User booker, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerAndStatus(User booker, Status status, Pageable pageable);

    List<Booking> findAllByItem_OwnerOrderByStartDesc(User owner, Pageable pageable);

    List<Booking> findAllByItem_OwnerAndStartAfterOrderByStartDesc(User owner, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByItem_OwnerAndEndBeforeOrderByStartDesc(User owner, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItem_OwnerAndStatus(User owner, Status status, Pageable pageable);

    List<Booking> findAllByItem_IdIn(List<Long> itemsId, Pageable pageable);

    List<Booking> findAllByBookerAndItemAndStartBefore(User booker, Item item, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(User booker, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItem_OwnerAndStartBeforeAndEndAfterOrderByStartDesc(User owner, LocalDateTime start, LocalDateTime end, Pageable pageable);
}
