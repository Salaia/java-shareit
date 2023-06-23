package ru.practicum.shareit.item.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDtoInput;
import ru.practicum.shareit.item.dto.CommentDtoOutput;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsAndComments;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Validated
@Slf4j
@RequestMapping("/items")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemController {
    private static final String HEADER_SHARER = "X-Sharer-User-Id";
    ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader(HEADER_SHARER) Long ownerId,
                          @Validated(ItemDto.Create.class) @RequestBody ItemDto itemDto) {
        log.debug("Request received: create item.");
        return itemService.create(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@Validated(ItemDto.Update.class) @RequestBody ItemDto itemDto,
                          @PathVariable Long itemId,
                          @RequestHeader(HEADER_SHARER) Long ownerId) {
        itemDto.setId(itemId);
        log.debug("Request received: update item.");
        return itemService.update(itemDto, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBookingsAndComments findItemById(@PathVariable Long itemId,
                                                       @RequestHeader(HEADER_SHARER) Long userId) {
        log.debug("Request received: find item by id.");
        return itemService.findItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoWithBookingsAndComments> findAll(@RequestHeader(HEADER_SHARER) Long ownerId,
                                                        @RequestParam(defaultValue = "0") Integer from,
                                                        @RequestParam(defaultValue = "20") Integer size) {
        log.debug("Request received: find all items for owner.");
        return itemService.findAll(ownerId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> search(String text,
                                @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                @RequestParam(defaultValue = "20") @PositiveOrZero Integer size) {
        log.debug("Request received: search for item.");
        return itemService.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoOutput createComment(@PathVariable Long itemId, @RequestHeader(HEADER_SHARER) Long bookerId,
                                          @RequestBody CommentDtoInput commentDto) {
        log.debug("Request received: create comment.");
        return itemService.createComment(itemId, bookerId, commentDto);
    }
}
