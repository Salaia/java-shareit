package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;

import java.util.List;

public interface BookingService {
    BookingDtoOutput create(BookingDtoInput bookingDto, Long bookerId);

    BookingDtoOutput setApprove(Long ownerId, boolean approve, Long bookingId);

    BookingDtoOutput findById(Long bookingId, Long userId);

    List<BookingDtoOutput> findAll(Long userId, String state, Integer from, Integer size);

    List<BookingDtoOutput> findAllByOwner(Long ownerId, String state, Integer from, Integer size);
}
