package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Data
@Builder
public class ItemRequestDtoOutput {
    Long id;
    String description;
    LocalDateTime created;
    List<ItemDto> items;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemRequestDtoOutput output = (ItemRequestDtoOutput) o;
        return Objects.equals(id, output.id) && Objects.equals(description, output.description);
    }
}
