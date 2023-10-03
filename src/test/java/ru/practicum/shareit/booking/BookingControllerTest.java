package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.booking.service.BookingDtoMapper;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Booking controller")
@SpringBootTest
@AutoConfigureMockMvc
public class BookingControllerTest {
    private static final String xSharerUserId = "X-Sharer-User-Id";
    @MockBean
    BookingService bookingService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private BookingDtoMapper bookingDtoMapper;
    private BookingDtoOnCreate bookingDtoOnCreate;
    private BookingDtoAfterCreate bookingDtoAfterCreate;
    private BookingDtoAfterApproving bookingDtoAfterApproving;
    private Item item;
    private User booker;
    private Booking booking;
    private LocalDateTime start = LocalDateTime.now().plusHours(1);
    private LocalDateTime end = start.plusHours(1);

    @BeforeEach
    void init() {
        booking = new Booking();

        booking.setId(1L);
        booking.setStart(start);
        booking.setEnd(end.plusHours(1));
        booking.setStatus(Status.WAITING);

        booker = new User();

        booker.setId(1L);
        booker.setName("name");
        booker.setEmail("booker@booker.ru");

        booking.setBooker(booker);

        item = new Item();

        item.setId(1L);
        item.setName("item name");
        item.setAvailable(true);

        booking.setItem(item);

        bookingDtoOnCreate = bookingDtoMapper.toDtoOnCreate(booking);
        bookingDtoAfterCreate = bookingDtoMapper.toDtoAfterCreate(booking);
        bookingDtoAfterApproving = bookingDtoMapper.toDtoAfterApproving(booking);
    }

    @Test
    void shouldCreateBooking() throws Exception {
        when(bookingService.createBooking(Mockito.any(), Mockito.anyLong()))
                .thenReturn(bookingDtoAfterCreate);

        mockMvc.perform(
                        post("/bookings")
                                .header(xSharerUserId, 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(bookingDtoOnCreate))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDtoAfterCreate)))
                .andExpect(jsonPath("$.id", is(bookingDtoAfterCreate.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDtoAfterCreate.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoAfterCreate.getBooker().getId()), Long.class));
    }

    @Test
    void shouldConfirmBooking() throws Exception {
        when(bookingService.confirmBooking(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean()))
                .thenReturn(bookingDtoAfterApproving);

        mockMvc.perform(
                        patch("/bookings/{bookingId}", 1L)
                                .header(xSharerUserId, 1L)
                                .param("approved", "true")
                )
                .andExpect(content().json(mapper.writeValueAsString(bookingDtoAfterApproving)))
                .andExpect(jsonPath("$.id", is(bookingDtoAfterApproving.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDtoAfterApproving.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoAfterApproving.getBooker().getId()), Long.class));
    }

    @Test
    void shouldReturnBooking() throws Exception {
        when(bookingService.getBookingById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(bookingDtoAfterApproving);

        mockMvc.perform(
                        get("/bookings/{bookingId}", 1L)
                                .header(xSharerUserId, 1L)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDtoAfterApproving)))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoAfterApproving.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDtoAfterApproving.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.id", is(bookingDtoAfterApproving.getId()), Long.class));
    }

    @Test
    void shouldReturnUserBookings() throws Exception {
        when(bookingService.getUserBookings(Mockito.anyLong(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(bookingDtoAfterCreate));

        mockMvc.perform(
                        get("/bookings")
                                .header(xSharerUserId, 1L)
                                .param("state", "ALL")
                                .param("from", "0")
                                .param("size", "20")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDtoAfterCreate))))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDtoAfterCreate.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDtoAfterCreate.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].id", is(bookingDtoAfterCreate.getId()), Long.class));
    }

    @Test
    void shouldReturnBookingsByOwner() throws Exception {
        when(bookingService.getBookingsByOwner(Mockito.anyLong(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(bookingDtoAfterCreate));

        mockMvc.perform(
                        get("/bookings/owner")
                                .header(xSharerUserId, 1L)
                                .param("state", "ALL")
                                .param("from", "0")
                                .param("size", "20")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDtoAfterCreate))))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDtoAfterCreate.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDtoAfterCreate.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].id", is(bookingDtoAfterCreate.getId()), Long.class));
    }

    @Test
    void shouldThrowBadBookingStatus() throws Exception {
        mockMvc.perform(
                        get("/bookings/owner")
                                .header(xSharerUserId, 1L)
                                .param("state", "whaaatisit")
                                .param("from", "0")
                                .param("size", "20")
                )
                .andExpect(status().isInternalServerError());
    }
}
