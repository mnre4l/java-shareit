package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.UserDidNotBookingItemException;
import ru.practicum.shareit.exception.model.UserIsNotItemOwnerException;
import ru.practicum.shareit.item.model.*;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.service.ItemDtoMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
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
@DisplayName("Item service")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {
    private final UserRepository userRepository;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;
    private final EntityManager em;
    User itemOwner;
    Item item;
    @Autowired
    ItemDtoMapper mapper;

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
    }

    @Test
    void shouldCreateItem() {
        ItemDto itemDto = itemService.createItem(mapper.toDto(item), itemOwner.getId());

        assertThat(itemDto.getName(), equalTo(item.getName()));
        assertThat(itemDto.getAvailable(), equalTo(item.getAvailable()));
        assertThat(itemDto.getDescription(), equalTo(item.getDescription()));
    }

    @Test
    void shouldThrowNotFoundWhenCreateItemWithUnknownOwner() {
        User unknown = new User();

        unknown.setId(Long.MAX_VALUE);
        assertThrows(NotFoundException.class, () -> {
            itemService.createItem(mapper.toDto(item), unknown.getId());
        });
    }

    @Test
    void shouldReturnItemWhenGetById() {
        ItemDto itemDto = itemService.createItem(mapper.toDto(item), itemOwner.getId());
        ItemInfoDto itemInfoDto = itemService.getItemDtoById(itemDto.getId(), itemOwner.getId());

        assertThat(itemDto.getName(), equalTo(itemInfoDto.getName()));
        assertThat(itemDto.getDescription(), equalTo(itemInfoDto.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(itemInfoDto.getAvailable()));
    }

    @Test
    void shouldThrowNotFoundWhenGetByBadId() {
        assertThrows(NotFoundException.class, () -> {
            itemService.getItemDtoById(Long.MAX_VALUE, itemOwner.getId());
        });
    }

    @Test
    void shouldThrowNotFoundWhenGetByUnknownUser() {
        ItemDto itemDto = itemService.createItem(mapper.toDto(item), itemOwner.getId());

        assertThrows(NotFoundException.class, () -> {
            itemService.getItemDtoById(itemDto.getId(), Long.MAX_VALUE);
        });
    }

    @Test
    void shouldSetNextAndLastBookings() {
        ItemDto itemDto = itemService.createItem(mapper.toDto(item), itemOwner.getId());

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@booker.ru");

        booker = userRepository.save(booker);

        Booking nextBooking = new Booking();

        nextBooking.setItem(mapper.fromDto(itemDto));
        nextBooking.setStart(LocalDateTime.now().plusHours(1));
        nextBooking.setEnd(LocalDateTime.now().plusHours(2));
        nextBooking.setStatus(Status.APPROVED);
        nextBooking.setBooker(booker);
        bookingRepository.save(nextBooking);

        Booking lastBooking = new Booking();

        lastBooking.setItem(mapper.fromDto(itemDto));
        lastBooking.setStart(LocalDateTime.now().minusHours(2));
        lastBooking.setEnd(LocalDateTime.now().minusHours(1));
        lastBooking.setStatus(Status.APPROVED);
        lastBooking.setBooker(booker);
        bookingRepository.save(lastBooking);

        ItemInfoDto itemDtoWithBooking = itemService.getItemDtoById(itemDto.getId(), itemOwner.getId());

        assertThat(itemDtoWithBooking.getLastBooking().getBookerId(), equalTo(lastBooking.getBooker().getId()));
        assertThat(itemDtoWithBooking.getLastBooking().getStart(), equalTo(lastBooking.getStart()));
        assertThat(itemDtoWithBooking.getLastBooking().getEnd(), equalTo(lastBooking.getEnd()));
        assertThat(itemDtoWithBooking.getNextBooking().getBookerId(), equalTo(nextBooking.getBooker().getId()));
        assertThat(itemDtoWithBooking.getNextBooking().getStart(), equalTo(nextBooking.getStart()));
        assertThat(itemDtoWithBooking.getNextBooking().getEnd(), equalTo(nextBooking.getEnd()));
    }

    @Test
    void shouldReturnAllItems() {
        ItemDto itemDto = itemService.createItem(mapper.toDto(item), itemOwner.getId());
        ItemDto itemDtoTwo = itemService.createItem(mapper.toDto(item), itemOwner.getId());

        List<ItemDto> items = itemService.getAll();

        assertThat(items, equalTo(List.of(itemDto, itemDtoTwo)));
    }

    @Test
    void shouldUpdateItem() {
        ItemDto itemDto = itemService.createItem(mapper.toDto(item), itemOwner.getId());

        itemDto.setDescription("updated");

        itemService.updateItem(itemDto.getId(), itemOwner.getId(), itemDto);
        ItemInfoDto updatedItemDto = itemService.getItemDtoById(itemDto.getId(), itemOwner.getId());

        assertThat(updatedItemDto.getDescription(), equalTo(itemDto.getDescription()));
    }

    @Test
    void shouldNotUpdateItemWhenUserIsNotOwner() {
        ItemDto itemDto = itemService.createItem(mapper.toDto(item), itemOwner.getId());

        itemDto.setDescription("updated");

        assertThrows(UserIsNotItemOwnerException.class, () -> {
            itemService.updateItem(itemDto.getId(), Long.MAX_VALUE, itemDto);
        });
    }

    @Test
    void shouldNotUpdateItemWhenUnknownItem() {
        assertThrows(NotFoundException.class, () -> {
            itemService.updateItem(Long.MAX_VALUE, Long.MAX_VALUE, new ItemDto());
        });
    }

    @Test
    void shouldReturnItemsByOwner() {
        ItemDto itemDto = itemService.createItem(mapper.toDto(item), itemOwner.getId());
        List<ItemInfoDto> items = itemService.getItemsByOwnerId(itemOwner.getId());

        assertThat(items.size(), equalTo(1));

        ItemInfoDto itemInfoDto = items.get(0);

        assertThat(itemInfoDto.getId(), equalTo(itemDto.getId()));
    }

    @Test
    void shouldReturnEmptyListWhenSearchByBlankString() {
        itemService.createItem(mapper.toDto(item), itemOwner.getId());

        List<ItemDto> items = itemService.findItemsBy("", 0, 20);

        assertTrue(items.isEmpty());
    }

    @Test
    void shouldReturnItemWhenSearchByTest() {
        ItemDto itemDto = itemService.createItem(mapper.toDto(item), itemOwner.getId());

        item.setDescription("tututu");

        ItemDto itemDtoTwo = itemService.createItem(mapper.toDto(item), itemOwner.getId());

        String text = itemDto.getDescription();
        List<ItemDto> items = itemService.findItemsBy(text, 0, 20);

        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getId(), equalTo(itemDto.getId()));
    }

    @Test
    void shouldReturnItemModel() {
        ItemDto itemDto = itemService.createItem(mapper.toDto(item), itemOwner.getId());
        Item item2 = itemService.getItemById(itemDto.getId());

        assertThat(item.getOwner(), equalTo(item2.getOwner()));
        assertThat(item.getDescription(), equalTo(item2.getDescription()));
        assertThat(item.getName(), equalTo(item2.getName()));
    }

    @Test
    void shouldThrowNotFoundWhenGetItemModelWithBadId() {
        assertThrows(NotFoundException.class, () -> {
            itemService.getItemById(Long.MAX_VALUE);
        });
    }

    @Test
    void shouldNotAddCommentWhenUnknownUser() {
        assertThrows(NotFoundException.class, () -> {
            itemService.addComment(Long.MAX_VALUE, 1L, new CommentDto());
        });
    }

    @Test
    void shouldNotAddCommentWhenUnknownItem() {
        assertThrows(NotFoundException.class, () -> {
            itemService.addComment(itemOwner.getId(), Long.MAX_VALUE, new CommentDto());
        });
    }

    @Test
    void shouldNotAddCommentWhenUserDidNotBookedItem() {
        ItemDto itemDto = itemService.createItem(mapper.toDto(item), itemOwner.getId());

        User notBooker = new User();
        notBooker.setEmail("liar@liar.com");
        notBooker.setName("liar");
        notBooker = userRepository.save(notBooker);
        Long notBookerId = notBooker.getId();

        assertThrows(UserDidNotBookingItemException.class, () -> {
            itemService.addComment(notBookerId, itemDto.getId(), new CommentDto());
        });
    }

    @Test
    void shouldAddComment() {
        ItemDto itemDto = itemService.createItem(mapper.toDto(item), itemOwner.getId());
        item.setId(itemDto.getId());

        User booker = new User();
        booker.setEmail("booker@booker.com");
        booker.setName("booker");
        booker = userRepository.save(booker);

        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(1));
        booking.setStatus(Status.APPROVED);

        booking = bookingRepository.save(booking);

        CommentDto commentDto = new CommentDto();

        commentDto.setText("nice item");

        commentDto = itemService.addComment(booker.getId(), itemDto.getId(), commentDto);

        TypedQuery<Comment> query = em.createQuery("Select c from Comment c where id =:id", Comment.class);
        Comment comment = query.setParameter("id", commentDto.getId()).getSingleResult();

        assertThat(commentDto.getText(), equalTo(comment.getText()));
    }
}
