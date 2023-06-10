package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class ItemRequestDtoInput {
    @NotNull
    //@Max(2000)
    String description;
}
