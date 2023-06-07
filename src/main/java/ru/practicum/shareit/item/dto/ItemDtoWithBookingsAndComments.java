package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.request.ItemRequest;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
public class ItemDtoWithBookingsAndComments { // Это ужасно длинное название. Думала ItemDtoOutput или ItemDtoExtended - но так названия совсем не говорящие...
    Long id;
    String name;
    String description;

    Boolean available;
    ItemRequest request;

    BookingDtoShort lastBooking;
    BookingDtoShort nextBooking;

    List<CommentDtoOutput> comments = new ArrayList<>();
}
