package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByItem_IdAndStatus(Long itemId, Status status, Pageable pageable);

    List<Booking> findAllByBooker_IdOrderByStartDesc(Long bookerId, Pageable pageable);

    List<Booking> findAllByBooker_IdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByBooker_IdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBooker_IdAndStatus(Long bookerId, Status status, Pageable pageable);

    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(Long ownerId, Pageable pageable);

    List<Booking> findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByItem_Owner_IdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItem_Owner_IdAndStatus(Long ownerId, Status status, Pageable pageable);

    List<Booking> findAllByItem_IdIn(List<Long> itemsId, Pageable pageable);

    List<Booking> findAllByBooker_IdAndItem_IdAndStartBefore(Long bookerId, Long itemId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long id, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long id, LocalDateTime start, LocalDateTime end, Pageable pageable);
}
