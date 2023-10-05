package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.ItemRequestDtoAfterCreate;
import ru.practicum.shareit.request.model.ItemRequestDtoInfo;
import ru.practicum.shareit.request.model.ItemRequestDtoOnCreate;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoAfterCreate createRequest(Long userId, ItemRequestDtoOnCreate request);

    List<ItemRequestDtoInfo> getRequestsByOwner(Long ownerId);

    ItemRequestDtoInfo getRequestById(Long requestId, Long userRequestFromId);

    List<ItemRequestDtoInfo> getAllRequests(Integer from, Integer size, Long userRequestFromId);
}
