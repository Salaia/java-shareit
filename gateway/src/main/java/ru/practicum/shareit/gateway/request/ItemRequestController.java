package ru.practicum.shareit.gateway.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.gateway.utils.Constants;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestController {

    final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(Constants.HEADER_SHARER) Long requesterId,
                                         @Valid @RequestBody ItemRequestDtoInput inputDto) {
        log.debug("Request received: create item request: {}\nfrom requester: {}", inputDto, requesterId);
        return requestClient.create(requesterId, inputDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllForRequester(@RequestHeader(Constants.HEADER_SHARER) Long requesterId,
                                                      @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                      @RequestParam(value = "size", defaultValue = "10") @PositiveOrZero Integer size) {
        log.debug("Request received: find all item requests for requester: {}" +
                "\nwith parameters: from: {}, size: {}", requesterId, from, size);
        return requestClient.findAllForRequester(requesterId, from, size);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllFromOthers(@RequestHeader(Constants.HEADER_SHARER) Long requesterId,
                                                    @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                    @RequestParam(value = "size", defaultValue = "10") @PositiveOrZero Integer size) {
        log.debug("Request received: find all other users' item requests.\nRequester: {}" +
                "\nRequest parameters: from: {}, size: {}", requesterId, from, size);
        return requestClient.findAllFromOthers(requesterId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@RequestHeader(Constants.HEADER_SHARER) Long userId,
                                           @PathVariable Long requestId) {
        log.debug("Request received: find item request by id (allowed for any user)." +
                "\nUser: {}, request: {}", userId, requestId);
        return requestClient.findById(userId, requestId);
    }
}
