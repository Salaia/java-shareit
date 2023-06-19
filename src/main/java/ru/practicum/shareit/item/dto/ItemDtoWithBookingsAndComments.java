package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@Setter
@Getter
public class ItemDtoWithBookingsAndComments {
    Long id;
    String name;
    String description;

    Boolean available;
    ItemRequest request;

    BookingDtoShort lastBooking;
    BookingDtoShort nextBooking;

    List<CommentDtoOutput> comments = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemDtoWithBookingsAndComments that = (ItemDtoWithBookingsAndComments) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description);
    }
}
