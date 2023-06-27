package ru.practicum.shareit.gateway.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.item.dto.CommentDtoInput;
import ru.practicum.shareit.gateway.item.dto.ItemDto;

import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemController {

    private static final String HEADER_SHARER = "X-Sharer-User-Id";
    final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(HEADER_SHARER) Long ownerId,
                                         @Validated(ru.practicum.shareit.gateway.item.dto.ItemDto.Create.class) @RequestBody ru.practicum.shareit.gateway.item.dto.ItemDto itemDto) {
        log.debug("Request received: create item: " + itemDto + "\nfor user: " + ownerId);
        return itemClient.create(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@Validated(ru.practicum.shareit.gateway.item.dto.ItemDto.Update.class) @RequestBody ItemDto itemDto,
                                         @PathVariable Long itemId,
                                         @RequestHeader(HEADER_SHARER) Long ownerId) {
        itemDto.setId(itemId);
        log.debug("Request received: update item: " + itemDto + ", id: " + itemId + "\nfor user: " + ownerId);
        return itemClient.update(itemDto, ownerId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemById(@PathVariable Long itemId,
                                               @RequestHeader(HEADER_SHARER) Long userId) {
        log.debug("Request received: find item by id: " + itemId + " from user: " + userId);
        return itemClient.findItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader(HEADER_SHARER) Long ownerId,
                                          @RequestParam(defaultValue = "0") Integer from,
                                          @RequestParam(defaultValue = "20") Integer size) {
        log.debug("Request received: find all items for owner: " + ownerId + "\nwith parameters:" +
                "\nfrom: " + from + ", size: " + size);
        return itemClient.findAll(ownerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader(HEADER_SHARER) Long searcherId, String text,
                                         @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                         @RequestParam(defaultValue = "20") @PositiveOrZero Integer size) {
        log.debug("Request received: search for item by text: " + text + "\nwith parameters: " +
                "\nfrom: " + from + ", size: " + size);
        return itemClient.search(searcherId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable Long itemId, @RequestHeader(HEADER_SHARER) Long bookerId,
                                                @RequestBody CommentDtoInput commentDto) {
        log.debug("Request received: create comment: " + commentDto +
                "\nfor item: " + itemId + ", from booker: " + bookerId);
        return itemClient.createComment(itemId, bookerId, commentDto);
    }
}
