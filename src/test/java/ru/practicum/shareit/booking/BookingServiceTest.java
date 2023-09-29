package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.model.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserBookingStates;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DisplayName("Booking service")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    private final EntityManager em;
    User itemOwner;
    Item item;
    User booker;

    @BeforeEach
    void init() {
        itemOwner = new User();

        itemOwner.setName("owner");
        itemOwner.setEmail("owner@owner.ru");

        itemOwner = userRepository.save(itemOwner);

        item = new Item();

        item.setName("item");
        item.setDescription("description");
        item.setOwner(itemOwner);
        item.setAvailable(true);

        item = itemRepository.save(item);

        booker = new User();

        booker.setName("booker");
        booker.setEmail("booker@booker.ru");

        booker = userRepository.save(booker);
    }

    @AfterEach
    void afterEach() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldCreateBooking() {
        BookingDtoOnCreate bookingDtoOnCreate = new BookingDtoOnCreate();

        bookingDtoOnCreate.setItemId(item.getId());
        bookingDtoOnCreate.setStart(LocalDateTime.now());
        bookingDtoOnCreate.setEnd(LocalDateTime.now().plusHours(1));

        BookingDtoAfterCreate booking = bookingService.createBooking(bookingDtoOnCreate, booker.getId());

        TypedQuery<Booking> query = em.createQuery("select b from Booking b where b.id = :id", Booking.class);
        Booking savedBooking = query.setParameter("id", booking.getId()).getSingleResult();

        assertThat(savedBooking.getId(), equalTo(booking.getId()));
        assertThat(savedBooking.getStart(), equalTo(booking.getStart()));
        assertThat(savedBooking.getItem().getId(), equalTo(booking.getItem().getId()));
    }

    @Test
    void shouldThrowNotFoundWhenCreateBookingByUnknownUser() {
        BookingDtoOnCreate bookingDtoOnCreate = new BookingDtoOnCreate();

        bookingDtoOnCreate.setItemId(item.getId());
        bookingDtoOnCreate.setStart(LocalDateTime.now());
        bookingDtoOnCreate.setEnd(LocalDateTime.now().plusHours(1));

        assertThrows(NotFoundException.class, () -> {
            bookingService.createBooking(bookingDtoOnCreate, Long.MAX_VALUE);
        });
    }

    @Test
    void shouldThrowNotFoundWhenCreateBookingWithUnknownItem() {
        BookingDtoOnCreate bookingDtoOnCreate = new BookingDtoOnCreate();

        bookingDtoOnCreate.setItemId(Long.MAX_VALUE);
        bookingDtoOnCreate.setStart(LocalDateTime.now());
        bookingDtoOnCreate.setEnd(LocalDateTime.now().plusHours(1));

        assertThrows(NotFoundException.class, () -> {
            bookingService.createBooking(bookingDtoOnCreate, booker.getId());
        });
    }

    @Test
    void shouldThrowItemNotAvailableWhenCreateBookingUnavailableItem() {
        item.setAvailable(false);
        itemRepository.save(item);

        BookingDtoOnCreate bookingDtoOnCreate = new BookingDtoOnCreate();

        bookingDtoOnCreate.setItemId(item.getId());
        bookingDtoOnCreate.setStart(LocalDateTime.now());
        bookingDtoOnCreate.setEnd(LocalDateTime.now().plusHours(1));

        assertThrows(ItemNotAvailableException.class, () -> {
            bookingService.createBooking(bookingDtoOnCreate, booker.getId());
        });
    }

    @Test
    void shouldNotCreateBookingWhenBookerIsOwner() {
        item.setOwner(booker);
        itemRepository.save(item);

        BookingDtoOnCreate bookingDtoOnCreate = new BookingDtoOnCreate();

        bookingDtoOnCreate.setItemId(item.getId());
        bookingDtoOnCreate.setStart(LocalDateTime.now());
        bookingDtoOnCreate.setEnd(LocalDateTime.now().plusHours(1));

        assertThrows(UserTryBookingItsItemException.class, () -> {
            bookingService.createBooking(bookingDtoOnCreate, booker.getId());
        });
    }

    @Test
    void shouldCreateBookingIfItemIsBookedForThisTime() {
        BookingDtoOnCreate bookingDtoOnCreate = new BookingDtoOnCreate();

        bookingDtoOnCreate.setItemId(item.getId());
        bookingDtoOnCreate.setStart(LocalDateTime.now());
        bookingDtoOnCreate.setEnd(LocalDateTime.now().plusHours(1));

        BookingDtoAfterCreate booking = bookingService.createBooking(bookingDtoOnCreate, booker.getId());
        bookingService.confirmBooking(booking.getId(), itemOwner.getId(), true);

        BookingDtoOnCreate bookingDtoOnCreateBadTime = new BookingDtoOnCreate();

        bookingDtoOnCreateBadTime.setItemId(item.getId());
        bookingDtoOnCreateBadTime.setStart(bookingDtoOnCreate.getStart().plusSeconds(1));
        bookingDtoOnCreateBadTime.setEnd(bookingDtoOnCreate.getStart().plusHours(1));

        assertThrows(ItemNotAvailableException.class, () -> {
            bookingService.createBooking(bookingDtoOnCreateBadTime, booker.getId());
        });
    }

    @Test
    void shouldConfirmBooking() {
        BookingDtoOnCreate bookingDtoOnCreate = new BookingDtoOnCreate();

        bookingDtoOnCreate.setItemId(item.getId());
        bookingDtoOnCreate.setStart(LocalDateTime.now());
        bookingDtoOnCreate.setEnd(LocalDateTime.now().plusHours(1));

        BookingDtoAfterCreate booking = bookingService.createBooking(bookingDtoOnCreate, booker.getId());

        bookingService.confirmBooking(booking.getId(), itemOwner.getId(), true);

        TypedQuery<Booking> query = em.createQuery("select b from Booking b where b.id = :id", Booking.class);
        Booking comfirmedBooking = query.setParameter("id", booking.getId())
                .getSingleResult();

        assertThat(comfirmedBooking.getStatus(), equalTo(Status.APPROVED));
    }

    @Test
    void shouldRejectBooking() {
        BookingDtoOnCreate bookingDtoOnCreate = new BookingDtoOnCreate();

        bookingDtoOnCreate.setItemId(item.getId());
        bookingDtoOnCreate.setStart(LocalDateTime.now());
        bookingDtoOnCreate.setEnd(LocalDateTime.now().plusHours(1));

        BookingDtoAfterCreate booking = bookingService.createBooking(bookingDtoOnCreate, booker.getId());

        bookingService.confirmBooking(booking.getId(), itemOwner.getId(), false);

        TypedQuery<Booking> query = em.createQuery("select b from Booking b where b.id = :id", Booking.class);
        Booking comfirmedBooking = query.setParameter("id", booking.getId())
                .getSingleResult();

        assertThat(comfirmedBooking.getStatus(), equalTo(Status.REJECTED));
    }

    @Test
    void shouldThrowNotFoundWhenConfirmUnknownBooking() {
        assertThrows(NotFoundException.class, () -> {
            bookingService.confirmBooking(Long.MAX_VALUE, itemOwner.getId(), true);
        });
    }

    @Test
    void shouldThrowNotFoundWhenConfirmByUnknownUser() {
        BookingDtoOnCreate bookingDtoOnCreate = new BookingDtoOnCreate();

        bookingDtoOnCreate.setItemId(item.getId());
        bookingDtoOnCreate.setStart(LocalDateTime.now());
        bookingDtoOnCreate.setEnd(LocalDateTime.now().plusHours(1));

        BookingDtoAfterCreate booking = bookingService.createBooking(bookingDtoOnCreate, booker.getId());

        assertThrows(NotFoundException.class, () -> {
            bookingService.confirmBooking(booking.getId(), Long.MAX_VALUE, true);
        });
    }

    @Test
    void shouldThrowsStatusCanNotBeChangedExceptionWhenConfirmBookingSecondTime() {
        BookingDtoOnCreate bookingDtoOnCreate = new BookingDtoOnCreate();

        bookingDtoOnCreate.setItemId(item.getId());
        bookingDtoOnCreate.setStart(LocalDateTime.now());
        bookingDtoOnCreate.setEnd(LocalDateTime.now().plusHours(1));

        BookingDtoAfterCreate booking = bookingService.createBooking(bookingDtoOnCreate, booker.getId());

        bookingService.confirmBooking(booking.getId(), itemOwner.getId(), false);

        assertThrows(StatusCanNotBeChangedException.class, () -> {
            bookingService.confirmBooking(booking.getId(), itemOwner.getId(), true);
        });
    }

    @Test
    void shouldNotConfirmBookingWhenUserIsNotItemOwner() {
        BookingDtoOnCreate bookingDtoOnCreate = new BookingDtoOnCreate();

        bookingDtoOnCreate.setItemId(item.getId());
        bookingDtoOnCreate.setStart(LocalDateTime.now());
        bookingDtoOnCreate.setEnd(LocalDateTime.now().plusHours(1));

        BookingDtoAfterCreate booking = bookingService.createBooking(bookingDtoOnCreate, booker.getId());

        assertThrows(UserIsNotItemOwnerException.class, () -> {
            bookingService.confirmBooking(booking.getId(), booker.getId(), true);
        });
    }

    @Test
    void shouldReturnBookingWhenRequestFromBooker() {
        BookingDtoOnCreate bookingDtoOnCreate = new BookingDtoOnCreate();

        bookingDtoOnCreate.setItemId(item.getId());
        bookingDtoOnCreate.setStart(LocalDateTime.now());
        bookingDtoOnCreate.setEnd(LocalDateTime.now().plusHours(1));

        BookingDtoAfterCreate booking = bookingService.createBooking(bookingDtoOnCreate, booker.getId());
        BookingDtoAfterApproving bookingDto = bookingService.getBookingById(booking.getId(), booker.getId());

        assertThat(bookingDto.getId(), equalTo(booking.getId()));
        assertThat(bookingDto.getBooker().getId(), equalTo(booker.getId()));
    }

    @Test
    void shouldReturnBookingWhenRequestFromOwner() {
        BookingDtoOnCreate bookingDtoOnCreate = new BookingDtoOnCreate();

        bookingDtoOnCreate.setItemId(item.getId());
        bookingDtoOnCreate.setStart(LocalDateTime.now());
        bookingDtoOnCreate.setEnd(LocalDateTime.now().plusHours(1));

        BookingDtoAfterCreate booking = bookingService.createBooking(bookingDtoOnCreate, booker.getId());
        BookingDtoAfterApproving bookingDto = bookingService.getBookingById(booking.getId(), booker.getId());

        assertThat(bookingDto.getId(), equalTo(booking.getId()));
        assertThat(bookingDto.getBooker().getId(), equalTo(booker.getId()));
        assertThat(bookingDto.getItem().getId(), equalTo(item.getId()));
    }

    @Test
    void shouldNotReturnBookingWhenUnknownUser() {
        BookingDtoOnCreate bookingDtoOnCreate = new BookingDtoOnCreate();

        bookingDtoOnCreate.setItemId(item.getId());
        bookingDtoOnCreate.setStart(LocalDateTime.now());
        bookingDtoOnCreate.setEnd(LocalDateTime.now().plusHours(1));

        BookingDtoAfterCreate booking = bookingService.createBooking(bookingDtoOnCreate, booker.getId());

        assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingById(booking.getId(), Long.MAX_VALUE);
        });
    }

    @Test
    void shouldNotReturnOwnerBookingsByStateWhenUnknownUser() {
        BookingDtoOnCreate bookingDtoOnCreate = new BookingDtoOnCreate();

        bookingDtoOnCreate.setItemId(item.getId());
        bookingDtoOnCreate.setStart(LocalDateTime.now());
        bookingDtoOnCreate.setEnd(LocalDateTime.now().plusHours(1));

        BookingDtoAfterCreate booking = bookingService.createBooking(bookingDtoOnCreate, booker.getId());

        assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingsByOwner(Long.MAX_VALUE, UserBookingStates.PAST, 0, 20);
        });
    }

    @Test
    void shouldReturnOwnerBookingsByStateCurrent() {
        BookingDtoOnCreate bookingDtoOnCreate = new BookingDtoOnCreate();

        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        bookingDtoOnCreate.setItemId(item.getId());
        bookingDtoOnCreate.setStart(start);
        bookingDtoOnCreate.setEnd(end);

        BookingDtoAfterCreate booking = bookingService.createBooking(bookingDtoOnCreate, booker.getId());

        for (UserBookingStates state : List.of(UserBookingStates.FUTURE, UserBookingStates.ALL, UserBookingStates.CURRENT,
                UserBookingStates.PAST)) {
            List<BookingDtoAfterCreate> bookings = bookingService.getBookingsByOwner(itemOwner.getId(), state, 0, 20);

            if (state.equals(UserBookingStates.CURRENT) || state.equals(UserBookingStates.ALL)) {
                assertThat(bookings.get(0).getId(), equalTo(booking.getId()));
            } else {
                assertTrue(bookings.isEmpty());
            }
        }
    }

    @Test
    void shouldReturnOwnerBookingsByStateFuture() {
        BookingDtoOnCreate bookingDtoOnCreate = new BookingDtoOnCreate();

        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(1);

        bookingDtoOnCreate.setItemId(item.getId());
        bookingDtoOnCreate.setStart(start);
        bookingDtoOnCreate.setEnd(end);

        BookingDtoAfterCreate booking = bookingService.createBooking(bookingDtoOnCreate, booker.getId());

        for (UserBookingStates state : List.of(UserBookingStates.FUTURE, UserBookingStates.ALL, UserBookingStates.CURRENT,
                UserBookingStates.PAST)) {
            List<BookingDtoAfterCreate> bookings = bookingService.getBookingsByOwner(itemOwner.getId(), state, 0, 20);

            if (state.equals(UserBookingStates.FUTURE) || state.equals(UserBookingStates.ALL)) {
                assertThat(bookings.get(0).getId(), equalTo(booking.getId()));
            } else {
                assertTrue(bookings.isEmpty());
            }
        }
    }

    @Test
    void shouldReturnOwnerBookingsByStatePast() {
        BookingDtoOnCreate bookingDtoOnCreate = new BookingDtoOnCreate();

        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = start.minusHours(1);

        bookingDtoOnCreate.setItemId(item.getId());
        bookingDtoOnCreate.setStart(start);
        bookingDtoOnCreate.setEnd(end);

        BookingDtoAfterCreate booking = bookingService.createBooking(bookingDtoOnCreate, booker.getId());

        for (UserBookingStates state : List.of(UserBookingStates.FUTURE, UserBookingStates.ALL, UserBookingStates.CURRENT,
                UserBookingStates.PAST)) {
            List<BookingDtoAfterCreate> bookings = bookingService.getBookingsByOwner(itemOwner.getId(), state, 0, 20);

            if (state.equals(UserBookingStates.PAST) || state.equals(UserBookingStates.ALL)) {
                assertThat(bookings.get(0).getId(), equalTo(booking.getId()));
            } else {
                assertTrue(bookings.isEmpty());
            }
        }
    }

    @Test
    void shouldReturnOwnerBookingsByStateRejected() {
        BookingDtoOnCreate bookingDtoOnCreate = new BookingDtoOnCreate();

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(1);

        bookingDtoOnCreate.setItemId(item.getId());
        bookingDtoOnCreate.setStart(start);
        bookingDtoOnCreate.setEnd(end);

        BookingDtoAfterCreate booking = bookingService.createBooking(bookingDtoOnCreate, booker.getId());
        bookingService.confirmBooking(booking.getId(), itemOwner.getId(), false);

        for (UserBookingStates state : UserBookingStates.values()) {
            System.out.println(state);
            List<BookingDtoAfterCreate> bookings = bookingService.getBookingsByOwner(itemOwner.getId(), state, 0, 20);

            if (state.equals(UserBookingStates.REJECTED) || state.equals(UserBookingStates.ALL)
                    || state.equals(UserBookingStates.CURRENT)) {
                assertThat(bookings.get(0).getId(), equalTo(booking.getId()));
            } else {
                assertTrue(bookings.isEmpty());
            }
        }
    }

    @Test
    void shouldNotReturnUserBookingsByStateWhenUnknownUser() {
        BookingDtoOnCreate bookingDtoOnCreate = new BookingDtoOnCreate();

        bookingDtoOnCreate.setItemId(item.getId());
        bookingDtoOnCreate.setStart(LocalDateTime.now());
        bookingDtoOnCreate.setEnd(LocalDateTime.now().plusHours(1));

        BookingDtoAfterCreate booking = bookingService.createBooking(bookingDtoOnCreate, booker.getId());

        assertThrows(NotFoundException.class, () -> {
            bookingService.getUserBookings(Long.MAX_VALUE, UserBookingStates.PAST, 0, 20);
        });
    }

    @Test
    void shouldReturnUserBookingsByStateCurrent() {
        BookingDtoOnCreate bookingDtoOnCreate = new BookingDtoOnCreate();

        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        bookingDtoOnCreate.setItemId(item.getId());
        bookingDtoOnCreate.setStart(start);
        bookingDtoOnCreate.setEnd(end);

        BookingDtoAfterCreate booking = bookingService.createBooking(bookingDtoOnCreate, booker.getId());

        for (UserBookingStates state : List.of(UserBookingStates.FUTURE, UserBookingStates.ALL, UserBookingStates.CURRENT,
                UserBookingStates.PAST)) {
            System.out.println(state);
            List<BookingDtoAfterCreate> bookings = bookingService.getUserBookings(booker.getId(), state, 0, 20);

            if (state.equals(UserBookingStates.CURRENT) || state.equals(UserBookingStates.ALL)) {
                assertThat(bookings.get(0).getId(), equalTo(booking.getId()));
            } else {
                assertTrue(bookings.isEmpty());
            }
        }
    }

    @Test
    void shouldReturnUserBookingsByStateFuture() {
        BookingDtoOnCreate bookingDtoOnCreate = new BookingDtoOnCreate();

        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(1);

        bookingDtoOnCreate.setItemId(item.getId());
        bookingDtoOnCreate.setStart(start);
        bookingDtoOnCreate.setEnd(end);

        BookingDtoAfterCreate booking = bookingService.createBooking(bookingDtoOnCreate, booker.getId());

        for (UserBookingStates state : List.of(UserBookingStates.FUTURE, UserBookingStates.ALL, UserBookingStates.CURRENT,
                UserBookingStates.PAST)) {
            System.out.println(state);
            List<BookingDtoAfterCreate> bookings = bookingService.getUserBookings(booker.getId(), state, 0, 20);

            if (state.equals(UserBookingStates.FUTURE) || state.equals(UserBookingStates.ALL)) {
                assertThat(bookings.get(0).getId(), equalTo(booking.getId()));
            } else {
                assertTrue(bookings.isEmpty());
            }
        }
    }

    @Test
    void shouldReturnUserBookingsByStatePast() {
        BookingDtoOnCreate bookingDtoOnCreate = new BookingDtoOnCreate();

        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = start.minusHours(1);

        bookingDtoOnCreate.setItemId(item.getId());
        bookingDtoOnCreate.setStart(start);
        bookingDtoOnCreate.setEnd(end);

        BookingDtoAfterCreate booking = bookingService.createBooking(bookingDtoOnCreate, booker.getId());

        for (UserBookingStates state : List.of(UserBookingStates.FUTURE, UserBookingStates.ALL, UserBookingStates.CURRENT,
                UserBookingStates.PAST)) {
            System.out.println(state);
            List<BookingDtoAfterCreate> bookings = bookingService.getUserBookings(booker.getId(), state, 0, 20);

            if (state.equals(UserBookingStates.PAST) || state.equals(UserBookingStates.ALL)) {
                assertThat(bookings.get(0).getId(), equalTo(booking.getId()));
            } else {
                assertTrue(bookings.isEmpty());
            }
        }
    }

    @Test
    void shouldReturnUserBookingsByStateRejected() {
        BookingDtoOnCreate bookingDtoOnCreate = new BookingDtoOnCreate();

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(1);

        bookingDtoOnCreate.setItemId(item.getId());
        bookingDtoOnCreate.setStart(start);
        bookingDtoOnCreate.setEnd(end);

        BookingDtoAfterCreate booking = bookingService.createBooking(bookingDtoOnCreate, booker.getId());
        bookingService.confirmBooking(booking.getId(), itemOwner.getId(), false);

        for (UserBookingStates state : UserBookingStates.values()) {
            System.out.println(state);
            List<BookingDtoAfterCreate> bookings = bookingService.getUserBookings(booker.getId(), state, 0, 20);

            if (state.equals(UserBookingStates.REJECTED) || state.equals(UserBookingStates.ALL)
                    || state.equals(UserBookingStates.CURRENT)) {
                assertThat(bookings.get(0).getId(), equalTo(booking.getId()));
            } else {
                assertTrue(bookings.isEmpty());
            }
        }
    }
}
