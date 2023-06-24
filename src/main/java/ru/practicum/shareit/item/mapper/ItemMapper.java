package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsAndComments;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemMapper {

    public ItemDto toItemDto(Item item) {
        ItemDto result = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
        if (item.getRequest() != null) {
            result.setRequestId(item.getRequest().getId());
        }
        return result;
    }

    public Item toItem(ItemDto itemDto, User owner, ItemRequest request) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(owner);
        item.setRequest(request);
        return item;
    }

    public List<ItemDto> itemDtoList(List<Item> items) {
        List<ItemDto> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(toItemDto(item));
        }
        return dtos;
    }

    public ItemDtoWithBookingsAndComments toDtoExtended(Item item, Booking lastBooking, Booking nextBooking, List<Comment> comments) {
        ItemDtoWithBookingsAndComments result = new ItemDtoWithBookingsAndComments();
        result.setId(item.getId());
        result.setName(item.getName());
        result.setDescription(item.getDescription());
        result.setAvailable(item.getAvailable());
        result.setRequest(item.getRequest());
        if (lastBooking != null) {
            result.setLastBooking(BookingMapper.toShortDto(lastBooking));
        }
        if (nextBooking != null) {
            result.setNextBooking(BookingMapper.toShortDto(nextBooking));
        }
        if (comments != null && !comments.isEmpty()) {
            result.setComments(CommentMapper.toCommentDtoList(comments));
        }
        return result;
    }
}
