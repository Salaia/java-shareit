package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemController {
    private static final String HEADER_SHARER = "X-Sharer-User-Id";
    ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader(HEADER_SHARER) Long ownerId,
                          @Validated(ItemDto.Create.class) @RequestBody ItemDto itemDto) {
        return itemService.create(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@Validated(ItemDto.Update.class) @RequestBody ItemDto itemDto,
                          @PathVariable Long itemId,
                          @RequestHeader(HEADER_SHARER) Long ownerId) {
        itemDto.setId(itemId);
        return itemService.update(itemDto, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBookingsAndComments findItemById(@PathVariable Long itemId,
                                                       @RequestHeader(HEADER_SHARER) Long userId) {
        return itemService.findItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoWithBookingsAndComments> findAll(@RequestHeader(HEADER_SHARER) Long ownerId) {
        return itemService.findAll(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(String text) {
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoOutput createComment(@PathVariable Long itemId, @RequestHeader(HEADER_SHARER) Long bookerId,
                                          @RequestBody CommentDtoInput commentDto) {
        return itemService.createComment(itemId, bookerId, commentDto);
    }
}
