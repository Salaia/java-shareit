package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class CommentDtoInput {

    @NotNull
    @Max(5000)
    String text;
}
