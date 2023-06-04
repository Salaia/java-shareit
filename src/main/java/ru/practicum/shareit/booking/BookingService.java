package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    BookingDtoOutput create(BookingDtoInput bookingDto, Long bookerId);

    BookingDtoOutput setApprove(Long ownerId, boolean approve, Long bookingId);

    BookingDtoOutput findById(Long bookingId, Long userId);

    List<BookingDtoOutput> findAll(Long userId, String state);

    List<BookingDtoOutput> findAllByOwner(Long ownerId, String state);
}
