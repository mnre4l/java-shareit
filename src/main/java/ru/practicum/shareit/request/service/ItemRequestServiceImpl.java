package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.service.ItemDtoMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDtoAfterCreate;
import ru.practicum.shareit.request.model.ItemRequestDtoInfo;
import ru.practicum.shareit.request.model.ItemRequestDtoOnCreate;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utilities.models.Page;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestDtoMapper mapper;
    private final ItemDtoMapper itemDtoMapper;

    @Override
    public ItemRequestDtoAfterCreate createRequest(Long userId, ItemRequestDtoOnCreate request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользовать id = " + userId));
        ItemRequest itemRequest = mapper.fromDto(request);

        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setUser(user);
        log.info("Сохраняем запрос: {}", itemRequest);
        itemRequestRepository.save(itemRequest);
        log.info("Сохранено: {}", itemRequest);

        ItemRequestDtoAfterCreate itemRequestDtoAfterCreate = mapper.fromDroAfterCreate(itemRequest);

        return itemRequestDtoAfterCreate;
    }

    @Override
    public List<ItemRequestDtoInfo> getRequestsByOwner(Long ownerId) {
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Не найден пользовать id = " + ownerId));
        List<ItemRequest> userRequests = itemRequestRepository.getItemRequestByUser_IdOrderByCreated(ownerId);

        log.info("Найдены запросы: {}", userRequests);

        List<ItemRequestDtoInfo> userRequestsDto = userRequests.stream()
                .map(itemRequest -> {
                    List<ItemRequestDtoInfo.ItemForRequestDto> itemsForRequest = new ArrayList<>();
                    if (itemRequest.getItems() != null) {
                        itemsForRequest = itemRequest.getItems().stream()
                                .map(itemDtoMapper::toItemForRequestDto)
                                .collect(Collectors.toList());
                    } else {
                        itemsForRequest = null;
                    }
                    ItemRequestDtoInfo itemRequestDtoInfo = mapper.toDtoInfo(itemRequest);
                    itemRequestDtoInfo.setItems(itemsForRequest);
                    return itemRequestDtoInfo;
                })
                .collect(Collectors.toList());
        log.info("Возвращен список запросов: {}", userRequestsDto);
        return userRequestsDto;
    }

    @Override
    public ItemRequestDtoInfo getRequestById(Long requestId, Long userRequestFromId) {
        User user = userRepository.findById(userRequestFromId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id = " + userRequestFromId));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Не найден запрос id = " + requestId));

        log.info("Найден запрос: {}", itemRequest);

        ItemRequestDtoInfo itemRequestDtoInfo = mapper.toDtoInfo(itemRequest);

        if (itemRequest.getItems() != null) {
            itemRequestDtoInfo.setItems(itemRequest.getItems().stream()
                    .map(itemDtoMapper::toItemForRequestDto)
                    .collect(Collectors.toList()));
        }
        return mapper.toDtoInfo(itemRequest);
    }

    @Override
    public List<ItemRequestDtoInfo> getAllRequests(Integer from, Integer size, Long userRequestFrom) {
        User requester = userRepository.findById(userRequestFrom)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id = " + userRequestFrom));
        List<ItemRequestDtoInfo> requests = itemRequestRepository
                .getAllByUserNot(requester, new Page(from, size, Sort.unsorted()))
                .stream()
                .map(itemRequest -> {
                    List<ItemRequestDtoInfo.ItemForRequestDto> items = new ArrayList<>();
                    if (itemRequest.getItems() != null) {
                        items = itemRequest.getItems().stream()
                                .map(itemDtoMapper::toItemForRequestDto)
                                .collect(Collectors.toList());
                    }
                    ItemRequestDtoInfo itemRequestDto = mapper.toDtoInfo(itemRequest);
                    itemRequestDto.setItems(items);
                    return itemRequestDto;
                })
                .collect(Collectors.toList());

        return requests;
    }
}
