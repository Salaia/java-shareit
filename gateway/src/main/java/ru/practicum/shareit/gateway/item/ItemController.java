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
import ru.practicum.shareit.gateway.utils.Constants;

import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemController {

    final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(Constants.HEADER_SHARER) Long ownerId,
                                         @Validated(ru.practicum.shareit.gateway.item.dto.ItemDto.Create.class) @RequestBody ru.practicum.shareit.gateway.item.dto.ItemDto itemDto) {
        log.debug("Request received: create item: {}\nfor user: {}", itemDto, ownerId);
        return itemClient.create(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@Validated(ru.practicum.shareit.gateway.item.dto.ItemDto.Update.class) @RequestBody ItemDto itemDto,
                                         @PathVariable Long itemId,
                                         @RequestHeader(Constants.HEADER_SHARER) Long ownerId) {
        itemDto.setId(itemId);
        log.debug("Request received: update item: {}, id: {}\nfor user: {}", itemDto, itemId, ownerId);
        return itemClient.update(itemDto, ownerId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemById(@PathVariable Long itemId,
                                               @RequestHeader(Constants.HEADER_SHARER) Long userId) {
        log.debug("Request received: find item by id: {}, from user: {}", itemId, userId);
        return itemClient.findItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader(Constants.HEADER_SHARER) Long ownerId,
                                          @RequestParam(defaultValue = "0") Integer from,
                                          @RequestParam(defaultValue = "20") Integer size) {
        log.debug("Request received: find all items for owner: {}\nwith parameters:" +
                "\nfrom: {}, size: {}", ownerId, from, size);
        return itemClient.findAll(ownerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader(Constants.HEADER_SHARER) Long searcherId, String text,
                                         @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                         @RequestParam(defaultValue = "20") @PositiveOrZero Integer size) {
        log.debug("Request received: search for item by text: {}\nwith parameters: " +
                "\nfrom: {}, size: {}", text, from, size);
        return itemClient.search(searcherId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable Long itemId, @RequestHeader(Constants.HEADER_SHARER) Long bookerId,
                                                @RequestBody CommentDtoInput commentDto) {
        log.debug("Request received: create comment: {}" +
                "\nfor item: {}, from booker: {}", commentDto, itemId, bookerId);
        return itemClient.createComment(itemId, bookerId, commentDto);
    }
}
