package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutput;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoOutput create(Long requesterId, ItemRequestDtoInput inputDto);

    List<ItemRequestDtoOutput> findAllForRequester(Long requesterId, Integer from, Integer size);

    List<ItemRequestDtoOutput> findAllFromOthers(Long requesterId, Integer from, Integer size);

    ItemRequestDtoOutput findById(Long userId, Long requestId);
}
