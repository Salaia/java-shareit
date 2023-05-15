package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/*
 Бронируется вещь всегда на определённые даты.
 Владелец вещи обязательно должен подтвердить бронирование.
 */
@Data
public class Booking { // черновик - подробности жду в последующих ТЗ

    Long id;
    LocalDateTime start;
    LocalDateTime end;
    Item item;
    User booker;
    /*
status — статус бронирования. Может принимать одно из следующих значений:
WAITING — новое бронирование, ожидает одобрения,
APPROVED — бронирование подтверждено владельцем,
REJECTED — бронирование отклонено владельцем,
CANCELED — бронирование отменено создателем.
     */
}
