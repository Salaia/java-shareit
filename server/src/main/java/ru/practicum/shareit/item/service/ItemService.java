package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDtoInput;
import ru.practicum.shareit.item.dto.CommentDtoOutput;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsAndComments;

import java.util.List;

public interface ItemService {

    ItemDto create(ItemDto itemDto, Long ownerId);

    ItemDto update(ItemDto itemDto, Long ownerId);

    ItemDtoWithBookingsAndComments findItemById(Long itemId, Long userId);

    List<ItemDtoWithBookingsAndComments> findAll(Long ownerId, Integer from, Integer size);

    List<ItemDto> search(String text, Integer from, Integer size);

    CommentDtoOutput createComment(Long itemId, Long bookerId, CommentDtoInput commentDto);
}
