package ru.practicum.shareit.item;

import lombok.Data;

import java.time.Instant;

@Data
public class CommentDtoOutput {
    Long id;

    String text;

    String authorName;

    Instant created = Instant.now();
}
