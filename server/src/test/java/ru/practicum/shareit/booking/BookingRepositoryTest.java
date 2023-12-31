package ru.practicum.shareit.booking;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingRepositoryTest {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    User booker;
    User owner;
    Item item;
    Booking booking;

    @BeforeEach
    void init() {
        booker = new User();

        booker.setName("booker");
        booker.setEmail("booker@email.ru");
        booker = userRepository.save(booker);

        owner = new User();

        owner.setName("owner");
        owner.setEmail("owner@email.ru");
        owner = userRepository.save(owner);

        item = new Item();

        item.setOwner(owner);
        item.setAvailable(true);
        item.setDescription("description");
        item.setName("item");

        item = itemRepository.save(item);

        booking = new Booking();

        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(1));
        System.out.println(booker.getId());
    }

    @AfterEach
    void afterEach() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldFindBookingByItemAndStatus() {
        booking = bookingRepository.save(booking);

        List<Booking> result = bookingRepository
                .findAllByItemAndStatus(item, Status.WAITING, Pageable.unpaged());

        assertThat(result.size(), equalTo(1));

        Booking resultBooking = result.get(0);

        assertThat(resultBooking.getItem().getId(), equalTo(item.getId()));
        assertThat(resultBooking.getBooker().getId(), equalTo(booker.getId()));
        assertThat(resultBooking.getStatus(), equalTo(Status.WAITING));
    }

    @Test
    void shouldFindNothingWhenFindBookingByItemAndStatusAndBadStatus() {
        booking = bookingRepository.save(booking);
        Status status = Status.REJECTED;
        List<Booking> result = bookingRepository
                .findAllByItemAndStatus(item, status, Pageable.unpaged());

        assertThat(status, not(booking.getStatus()));
        assertThat(result.size(), equalTo(0));
    }

    @Test
    void shouldFindNothingWhenFindBookingByItemAndStatusAndBadItem() {
        booking = bookingRepository.save(booking);

        Item badItem = new Item();

        badItem.setName("bad item");
        badItem.setDescription("bad description");
        badItem.setOwner(owner);
        badItem.setAvailable(true);

        badItem = itemRepository.save(badItem);

        List<Booking> result = bookingRepository
                .findAllByItemAndStatus(badItem, Status.WAITING, Pageable.unpaged());

        assertThat(result.size(), equalTo(0));
    }

    @Test
    void shouldFindByBookerId() {
        booking = bookingRepository.save(booking);
        List<Booking> result = bookingRepository
                .findAllByBookerAndStatus(booker, booking.getStatus(), Pageable.unpaged());

        assertThat(result.size(), equalTo(1));

        Booking resultBooking = result.get(0);

        assertThat(resultBooking.getId(), equalTo(booking.getId()));
    }

    @Test
    void shouldFindNothingWhenBadBooker() {
        booking = bookingRepository.save(booking);

        User badBooker = new User();
        badBooker.setEmail("bad@bad.com");
        badBooker.setName("bad");
        badBooker = userRepository.save(badBooker);

        List<Booking> badRequestResult = bookingRepository
                .findAllByBookerAndStatus(badBooker, booking.getStatus(), Pageable.unpaged());

        assertThat(badRequestResult.size(), equalTo(0));
    }

    @Test
    void shouldFindByBookerAndStartAfter() {
        booking = bookingRepository.save(booking);
        LocalDateTime goodStart = booking.getStart().minusHours(1);

        List<Booking> result = bookingRepository
                .findAllByBookerAndStartAfterOrderByStartDesc(
                        booker,
                        goodStart,
                        Pageable.unpaged()
                );
        assertThat(result.get(0).getId(), equalTo(booking.getId()));
    }

    @Test
    void shouldFindNothingByBookerAndStartAfterWhenBadStart() {
        booking = bookingRepository.save(booking);
        LocalDateTime badStart = booking.getStart().plusHours(1);

        List<Booking> result = bookingRepository
                .findAllByBookerAndStartAfterOrderByStartDesc(
                        booker,
                        badStart,
                        Pageable.unpaged()
                );
        assertThat(result.size(), equalTo(0));
    }

    @Test
    void shouldFindByBookerAndEndBefore() {
        booking = bookingRepository.save(booking);
        LocalDateTime goodEnd = booking.getEnd().plusYears(999);

        List<Booking> result = bookingRepository
                .findAllByBookerAndEndBeforeOrderByStartDesc(
                        booker,
                        goodEnd,
                        Pageable.unpaged()
                );
        assertThat(result.get(0).getId(), equalTo(booking.getId()));
    }

    @Test
    void shouldFindNothingByBookerAndEndBeforeWhenBadEnd() {
        booking = bookingRepository.save(booking);
        LocalDateTime badEnd = booking.getEnd().minusYears(999);

        List<Booking> result = bookingRepository
                .findAllByBookerAndEndBeforeOrderByStartDesc(
                        booker,
                        badEnd,
                        Pageable.unpaged()
                );
        assertThat(result.size(), equalTo(0));
    }
}
