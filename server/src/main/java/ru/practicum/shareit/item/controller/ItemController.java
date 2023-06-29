package ru.practicum.shareit.item.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDtoInput;
import ru.practicum.shareit.item.dto.CommentDtoOutput;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsAndComments;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/items")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemController {
    private static final String HEADER_SHARER = "X-Sharer-User-Id";
    ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader(HEADER_SHARER) Long ownerId,
                          @RequestBody ItemDto itemDto) {
        log.debug("Request received: create item: " + itemDto + "\nfor user: " + ownerId);
        return itemService.create(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto,
                          @PathVariable Long itemId,
                          @RequestHeader(HEADER_SHARER) Long ownerId) {
        itemDto.setId(itemId);
        log.debug("Request received: update item: " + itemDto + ", id: " + itemId + "\nfor user: " + ownerId);
        return itemService.update(itemDto, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBookingsAndComments findItemById(@PathVariable Long itemId,
                                                       @RequestHeader(HEADER_SHARER) Long userId) {
        log.debug("Request received: find item by id: " + itemId + " from user: " + userId);
        return itemService.findItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoWithBookingsAndComments> findAll(@RequestHeader(HEADER_SHARER) Long ownerId,
                                                        @RequestParam Integer from,
                                                        @RequestParam Integer size) {
        log.debug("Request received: find all items for owner: " + ownerId + "\nwith parameters:" +
                "\nfrom: " + from + ", size: " + size);
        return itemService.findAll(ownerId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> search(String text,
                                @RequestParam Integer from,
                                @RequestParam Integer size) {
        log.debug("Request received: search for item by text: " + text + "\nwith parameters: " +
                "\nfrom: " + from + ", size: " + size);
        return itemService.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoOutput createComment(@PathVariable Long itemId, @RequestHeader(HEADER_SHARER) Long bookerId,
                                          @RequestBody CommentDtoInput commentDto) {
        log.debug("Request received: create comment: " + commentDto +
                "\nfor item: " + itemId + ", from booker: " + bookerId);
        return itemService.createComment(itemId, bookerId, commentDto);
    }
}
