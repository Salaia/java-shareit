package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutput;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    public static ItemRequest toModel(ItemRequestDtoInput input, User requester) {
        ItemRequest result = new ItemRequest();
        result.setDescription(input.getDescription());
        result.setRequester(requester);
        return result;
    }

    public static ItemRequestDtoOutput toDtoOutput(ItemRequest request) {
        ItemRequestDtoOutput result = new ItemRequestDtoOutput();
        result.setId(request.getId());
        result.setDescription(request.getDescription());
        result.setCreated(request.getCreated());
        return result;
    }

    public static List<ItemRequestDtoOutput> toDtoList(List<ItemRequest> requests) {
        return requests.stream()
                .map(ItemRequestMapper::toDtoOutput)
                .collect(Collectors.toList());
    }
}