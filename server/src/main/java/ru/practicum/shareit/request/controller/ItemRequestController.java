package ru.practicum.shareit.request.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutput;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemRequestController {

    private static final String HEADER_SHARER = "X-Sharer-User-Id";
    ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDtoOutput create(@RequestHeader(HEADER_SHARER) Long requesterId,
                                       @RequestBody ItemRequestDtoInput inputDto) {
        log.debug("Request received: create item request: " + inputDto + "\nfrom requester: " + requesterId);
        return itemRequestService.create(requesterId, inputDto);
    }

    @GetMapping
    public List<ItemRequestDtoOutput> findAllForRequester(@RequestHeader(HEADER_SHARER) Long requesterId,
                                                          @RequestParam Integer from, @RequestParam Integer size) {
        log.debug("Request received: find all item requests for requester: " + requesterId +
                "\nwith parameters: from: " + from + ", size: " + size);
        return itemRequestService.findAllForRequester(requesterId, from, size);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoOutput> findAllFromOthers(@RequestHeader(HEADER_SHARER) Long requesterId,
                                                        @RequestParam Integer from, @RequestParam Integer size) {
        log.debug("Request received: find all other users' item requests.\nRequester: " + requesterId +
                "\nRequest parameters: from: " + from + ", size: " + size);
        return itemRequestService.findAllFromOthers(requesterId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoOutput findById(@RequestHeader(HEADER_SHARER) Long userId,
                                         @PathVariable Long requestId) {
        log.debug("Request received: find item request by id (allowed for any user)." +
                "\nUser: " + userId + ", request: " + requestId);
        return itemRequestService.findById(userId, requestId);
    }
}
