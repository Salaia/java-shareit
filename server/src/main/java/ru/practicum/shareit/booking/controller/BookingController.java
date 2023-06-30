package ru.practicum.shareit.booking.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingController {

    BookingService bookingService;
    private static final String HEADER_SHARER = "X-Sharer-User-Id";

    @PostMapping
    public BookingDtoOutput create(@RequestHeader(HEADER_SHARER) Long bookerId,
                                   @RequestBody BookingDtoInput bookingDto) {
        log.debug("Request received: create booking: " + bookingDto + "\nfor booker:" + bookerId);
        return bookingService.create(bookingDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOutput setApprove(@RequestHeader(HEADER_SHARER) Long ownerId,
                                       @RequestParam("approved") boolean approve,
                                       @PathVariable Long bookingId) {
        log.info("\nRequest received at server: set approve: " + approve + "\nfrom user: " + ownerId + "\nfor booking: " + bookingId);
        return bookingService.setApprove(ownerId, approve, bookingId);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOutput findById(@PathVariable Long bookingId,
                                     @RequestHeader(HEADER_SHARER) Long userId) {
        log.debug("Request received: find booking by id:" + bookingId + "\nfrom user: " + userId);
        return bookingService.findById(bookingId, userId);
    }


    @GetMapping
    public List<BookingDtoOutput> findAll(@RequestHeader(HEADER_SHARER) Long userId,
                                          @RequestParam(defaultValue = "ALL") String state,
                                          @RequestParam(defaultValue = "0") Integer from,
                                          @RequestParam(defaultValue = "10") Integer size) {
        log.debug("Request received: find all bookings for booker: " + userId + "\nwith request parameters:" +
                "\nfrom: " + from + ", size: " + size + ", state: " + state);
        return bookingService.findAll(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoOutput> findAllByOwner(@RequestHeader(HEADER_SHARER) Long ownerId,
                                                 @RequestParam(defaultValue = "ALL") String state,
                                                 @RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        log.debug("Request received: find all bookings for owner: " + ownerId +
                "\nwith parameters:\nfrom: " + from + ", size: " + size + ", state: " + state);
        return bookingService.findAllByOwner(ownerId, state, from, size);
    }

}
