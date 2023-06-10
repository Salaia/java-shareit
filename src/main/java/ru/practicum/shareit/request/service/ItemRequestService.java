package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutput;

import javax.validation.Valid;
import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoOutput create(Long requesterId, ItemRequestDtoInput inputDto);
    List<ItemRequestDtoOutput> findAllForRequester(Long requesterId);
    List<ItemRequestDtoOutput> findAllFromOthers(Long requesterId, Integer from, Integer size);
    ItemRequestDtoOutput findById(Long userId,Long requestId);
}
