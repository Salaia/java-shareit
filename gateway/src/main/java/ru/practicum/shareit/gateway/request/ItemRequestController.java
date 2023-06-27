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

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestController {
    private static final String HEADER_SHARER = "X-Sharer-User-Id";

    final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(HEADER_SHARER) Long requesterId,
                                         @Valid @RequestBody ItemRequestDtoInput inputDto) {
        log.debug("Request received: create item request: " + inputDto + "\nfrom requester: " + requesterId);
        return requestClient.create(requesterId, inputDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllForRequester(@RequestHeader(HEADER_SHARER) Long requesterId,
                                                      @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                      @RequestParam(value = "size", defaultValue = "10") @PositiveOrZero Integer size) {
        log.debug("Request received: find all item requests for requester: " + requesterId +
                "\nwith parameters: from: " + from + ", size: " + size);
        return requestClient.findAllForRequester(requesterId, from, size);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllFromOthers(@RequestHeader(HEADER_SHARER) Long requesterId,
                                                    @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                    @RequestParam(value = "size", defaultValue = "10") @PositiveOrZero Integer size) {
        log.debug("Request received: find all other users' item requests.\nRequester: " + requesterId +
                "\nRequest parameters: from: " + from + ", size: " + size);
        return requestClient.findAllFromOthers(requesterId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@RequestHeader(HEADER_SHARER) Long userId,
                                           @PathVariable Long requestId) {
        log.debug("Request received: find item request by id (allowed for any user)." +
                "\nUser: " + userId + ", request: " + requestId);
        return requestClient.findById(userId, requestId);
    }
}
