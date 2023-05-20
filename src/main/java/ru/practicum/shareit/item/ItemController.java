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
    ItemServiceImpl itemService;

    @PostMapping
    public ItemDto create(@RequestHeader(HEADER_SHARER) Long ownerId,
                          @Validated(ItemDto.Create.class) @RequestBody ItemDto itemDto) {
        itemDto.setOwnerId(ownerId);
        return itemService.create(itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@Validated(ItemDto.Update.class) @RequestBody ItemDto itemDto,
                          @PathVariable Long itemId,
                          @RequestHeader(HEADER_SHARER) Long ownerId) {
        itemDto.setId(itemId);
        itemDto.setOwnerId(ownerId);
        return itemService.update(itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto findItemById(@PathVariable Long itemId) {
        return itemService.findItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> findAll(@RequestHeader(HEADER_SHARER) Long ownerId) {
        return itemService.findAll(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(String text) {
        return itemService.search(text);
    }
}
