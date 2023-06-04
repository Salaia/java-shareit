package ru.practicum.shareit.item;

import javax.validation.Valid;
import java.util.List;

public interface ItemService {

    ItemDto create(@Valid ItemDto itemDto, Long ownerId);

    ItemDto update(ItemDto itemDto, Long ownerId);

    ItemDtoWithBookingsAndComments findItemById(Long itemId, Long userId);

    List<ItemDtoWithBookingsAndComments> findAll(Long ownerId);

    List<ItemDto> search(String text);

    CommentDtoOutput createComment(Long itemId, Long bookerId, CommentDtoInput commentDto);
}
