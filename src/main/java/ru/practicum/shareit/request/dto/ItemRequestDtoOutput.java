package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ItemRequestDtoOutput {
    Long id;
    String description;
    LocalDateTime created;
    List<ItemDto> items = new ArrayList<>();
}
