package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingDtoMapper;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.UserDidNotBookingItemException;
import ru.practicum.shareit.exception.model.UserIsNotItemOwnerException;
import ru.practicum.shareit.item.model.*;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utilities.models.Page;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    /**
     * Сервис пользоввателей
     */
    private final UserRepository userRepository;
    /**
     * Репозиторий вещей
     */
    private final ItemRepository itemRepository;
    /**
     * Маппер вещей
     */
    private final ItemDtoMapper itemMapper;
    /**
     * Маппер пользователей
     */
    private final BookingRepository bookingRepository;
    private final BookingDtoMapper bookingDtoMapper;
    private final CommentRepository commentRepository;
    private final CommentDtoMapper commentDtoMapper;

    @Override
    public ItemDto createItem(ItemDto itemDto, long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id = " + ownerId));
        Item item = itemMapper.fromDto(itemDto, owner);

        itemRepository.save(item);
        log.info("Создана вещь: {}", item);
        return itemMapper.toDto(item);
    }


    @Override
    public ItemInfoDto getItemDtoById(long id, Long userIdRequestFrom) {
        User user = userRepository.findById(userIdRequestFrom)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id = " + userIdRequestFrom));
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найден item с id = " + id));
        ItemInfoDto itemInfoDto = itemMapper.toItemInfoDto(item);

        log.info("Item owner id = {}, userIdRequestFrom = {}", item.getOwner().getId(), userIdRequestFrom);
        if (item.getOwner().getId().equals(userIdRequestFrom)) {
            setLastAndNextBookingsTo(List.of(itemInfoDto), LocalDateTime.now());
        }
        log.info("Возвращен item: {}", itemInfoDto);
        return itemInfoDto;
    }

    @Override
    public List<ItemDto> getAll() {
        List<ItemDto> items = itemRepository.findAll().stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
        return items;
    }

    @Override
    public ItemInfoDto updateItem(long itemId, long userIdRequestFrom, ItemDto updatedItem) {
        Item itemBeforeUpdate = getItemById(itemId);

        log.info("Найден item для обновления: {}", itemBeforeUpdate);

        checkIsUserItemOwner(itemBeforeUpdate, userIdRequestFrom);
        itemMapper.fromDto(updatedItem, itemBeforeUpdate);
        itemRepository.save(itemBeforeUpdate);
        return getItemDtoById(itemId, userIdRequestFrom);
    }

    @Override
    public List<ItemInfoDto> getItemsByOwnerId(long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id = " + userId));
        /*
        список Item принадлежащих пользователю
         */
        List<Item> userItems = itemRepository.findByOwnerOrderByIdAsc(owner);
        List<ItemInfoDto> userItemsDto = itemMapper.toItemInfoDto(userItems);

        setLastAndNextBookingsTo(userItemsDto, LocalDateTime.now());
        return userItemsDto;
    }

    @Override
    public List<ItemDto> findItemsBy(String text, Integer from, Integer size) {
        Page page = new Page(from, size, Sort.unsorted());
        //наверное, по хорошему нужно делать валидацию на someText, но тесты требуют именно пустой лист
        if (text.isBlank()) return Collections.emptyList();
        return itemRepository.findByAvailableTrueAndDescriptionContainingIgnoreCase(text, page).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }


    @Override
    public Item getItemById(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Не найден item с id = " + itemId));
    }

    /**
     * Метод предназначен для проверки, является ли пользователем владельцем вещи
     *
     * @param item   объект вещи
     * @param userId преподалагемый владелец вещи (id)
     */
    @Override
    public void checkIsUserItemOwner(Item item, long userId) {
        long ownerId = item.getOwner().getId();

        if (ownerId != userId)
            throw new UserIsNotItemOwnerException(String.format("Пользователь с id = %s не является " +
                    "владельцем вещи %s. Ее владелец пользователь id = %s", userId, item, ownerId));
    }

    @Override
    public CommentDto addComment(long userIdRequestFrom, Long itemId, CommentDto commentDto) {
        LocalDateTime now = LocalDateTime.now();

        User user = userRepository.findById(userIdRequestFrom)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id = " + userIdRequestFrom));
        Item item = getItemById(itemId);
        List<Booking> userBookings = bookingRepository
                .findAllByBookerAndItemAndStartBefore(user, item, now, Pageable.unpaged());

        log.info("Прошлые брони пользователя, который добавляет комментарий: {}", userBookings);

        if (userBookings.isEmpty()) {
            throw new UserDidNotBookingItemException("Пользователь ud = " + userIdRequestFrom + " не брал в аренду" +
                    " вещь id = " + itemId);
        }

        Comment comment = commentDtoMapper.fromDto(commentDto);
        comment.setCreated(now);
        comment.setAuthorName(user.getName());
        comment.setItemId(item.getId());

        return commentDtoMapper.toDto(commentRepository.save(comment));
    }

    private void setLastAndNextBookingsTo(List<ItemInfoDto> items, LocalDateTime moment) {
        log.info("Старт поиска следующей и прошлой брони для: {}", items);
        /*
        хеш-мапа вида: itemId -> Item
         */
        Map<Long, ItemInfoDto> itemsIdsAndTheirDto = items.stream()
                .collect(Collectors.toMap((itemDto) -> itemDto.getId(), itemDto -> itemDto));
        /*
        получаем брони для всех итемов
         */
        List<Booking> allBookingsForItems = bookingRepository
                .findAllByItem_IdIn(new ArrayList<>(itemsIdsAndTheirDto.keySet()), Pageable.unpaged());
        log.info("Получены брони для списка итемов: {}", allBookingsForItems);
        /*
        хеш-мапа вида: итем -> все брони на этот итем
         */
        HashMap<Long, List<Booking>> itemsIdsAndTheirBookings = new HashMap<>();
        /*
        для каждого итема определяем список броней на него и сохраняем в мапу id итема -> список броней на него
         */
        for (Booking booking : allBookingsForItems) {
            Long currentItemId = booking.getItem().getId();

            itemsIdsAndTheirBookings.computeIfAbsent(currentItemId, v -> new ArrayList<>());

            List<Booking> bookingsForCurrentItem = itemsIdsAndTheirBookings
                    .get(currentItemId);

            bookingsForCurrentItem.add(booking);
        }

        for (Long itemId : itemsIdsAndTheirBookings.keySet()) {
            log.info("Ищем следующую и прошлую брони для Item id = {}", itemId);

            Booking nextBooking = getNextTimeBookingFrom(moment, itemsIdsAndTheirBookings.get(itemId));
            Booking lastBooking = getLastTimeBookingFrom(moment, itemsIdsAndTheirBookings.get(itemId));

            log.info("Следующая бронь: {}, прошлая бронь: {}", nextBooking, lastBooking);

            ItemInfoDto itemInfoDto = itemsIdsAndTheirDto.get(itemId);
            ItemInfoDto.BookingDto bookingDtoNext = bookingDtoMapper.toDtoForItemInfo(nextBooking);

            log.info("Next booking: {}", bookingDtoNext);

            ItemInfoDto.BookingDto bookingDtoLast = bookingDtoMapper.toDtoForItemInfo(lastBooking);
            log.info("Next booking: {}", bookingDtoLast);

            itemInfoDto.setNextBooking(bookingDtoNext);
            itemInfoDto.setLastBooking(bookingDtoLast);

            log.info("itemInfoDto после установки прошлой и следующей брони: {}", itemInfoDto);
        }
    }

    private Booking getNextTimeBookingFrom(LocalDateTime moment, List<Booking> bookingList) {
        return bookingList.stream()
                .filter(b -> b.getStart().isAfter(moment))
                .filter(b -> (b.getStatus().equals(Status.APPROVED)) ||
                        b.getStatus().equals(Status.WAITING))
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);
    }

    private Booking getLastTimeBookingFrom(LocalDateTime moment, List<Booking> bookingList) {
        return bookingList.stream()
                .filter(b -> (b.getStart().isBefore(moment)))
                .filter(b -> (b.getStatus().equals(Status.APPROVED)) ||
                        b.getStatus().equals(Status.WAITING))
                .max(Comparator.comparing(Booking::getStart))
                .orElse(null);
    }

}
