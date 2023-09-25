package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingDtoMapper;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DisplayName("Item service")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingDtoMapper mapper;
    private final BookingService bookingService;
}
