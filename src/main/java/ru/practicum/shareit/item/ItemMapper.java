package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static Item toItem(ItemDto itemDto, User owner) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(owner);
        return item;
    }

    public static List<ItemDto> itemDtoList(List<Item> items) {
        List<ItemDto> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(toItemDto(item));
        }
        return dtos;
    }

    public static ItemDtoWithBookingsAndComments toDtoExtended(Item item, Booking lastBooking, Booking nextBooking, List<Comment> comments) {
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
