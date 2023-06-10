package ru.practicum.shareit.request.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutput;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemRequestController {

    private static final String HEADER_SHARER = "X-Sharer-User-Id";
    ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDtoOutput create(@RequestHeader(HEADER_SHARER) Long requesterId,
                                       @Valid @RequestBody ItemRequestDtoInput inputDto) {
        return itemRequestService.create(requesterId, inputDto);
    }

    @GetMapping
    public List<ItemRequestDtoOutput> findAllForRequester(@RequestHeader(HEADER_SHARER) Long requesterId) {
        return itemRequestService.findAllForRequester(requesterId);
    }

    @GetMapping("/all") // GET /requests/all?from={from}&size={size}
    public List<ItemRequestDtoOutput> findAllFromOthers(@RequestHeader(HEADER_SHARER) Long requesterId,
                                                        @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                        @RequestParam(value = "size", defaultValue = "10") @PositiveOrZero Integer size) {
        return itemRequestService.findAllFromOthers(requesterId, from, size);

    }

    @GetMapping("/{requestId}") // allowed for all users
    public ItemRequestDtoOutput findById(@RequestHeader(HEADER_SHARER) Long userId,
                                               @PathVariable Long requestId) {
        return itemRequestService.findById(userId, requestId);
    }
}
