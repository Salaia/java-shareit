package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/*
На случай, если нужной вещи на сервисе нет, у пользователей должна быть возможность оставлять запросы.
 Вдруг древний граммофон, который странно даже предлагать к аренде,
 неожиданно понадобится для театральной постановки.
По запросу можно будет добавлять новые вещи для шеринга.
 */
@Data
public class ItemRequest {
    Long id;
    String description;
    User requester;
    LocalDateTime created;
}
