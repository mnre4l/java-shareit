package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByItem_IdAndStatus(Long itemId, Status status);

    List<Booking> findAllByBooker_IdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByBooker_IdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime start);

    List<Booking> findAllByBooker_IdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime end);

    List<Booking> findAllByBooker_IdAndStartAfterAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBooker_IdAndStatus(Long bookerId, Status status);

    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(Long ownerId);

    List<Booking> findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime start);

    List<Booking> findAllByItem_Owner_IdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime end);

    List<Booking> findAllByItem_Owner_IdAndStartAfterAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItem_Owner_IdAndStatus(Long ownerId, Status status);

    List<Booking> findAllByItem_IdIn(List<Long> itemsId);

    List<Booking> findAllByBooker_IdAndItem_IdAndStartBefore(Long bookerId, Long itemId, LocalDateTime start);

    List<Booking> findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long id, LocalDateTime now, LocalDateTime now1);

    List<Booking> findAllByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long id, LocalDateTime now, LocalDateTime now1);
}
