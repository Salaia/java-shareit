package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.BookingSearchState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationExceptionCustom;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingServiceImpl implements BookingService {

    BookingRepository bookingRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;

    @Override
    public BookingDtoOutput create(BookingDtoInput bookingDto, Long bookerId) {
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new ValidationExceptionCustom("Booking start time must be before it's end.");
        }
        if (bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new ValidationExceptionCustom("Booking start time can't be equal to it's end.");
        }

        Optional<User> bookerOptional = userRepository.findById(bookerId);
        if (bookerOptional.isEmpty()) {
            throw new EntityNotFoundException("Not found: booker's id " + bookerId);
        }
        User booker = bookerOptional.get();

        Optional<Item> itemOptional = itemRepository.findById(bookingDto.getItemId());
        if (itemOptional.isEmpty()) {
            throw new EntityNotFoundException("Not found: item's id " + bookingDto.getItemId());
        }
        Item item = itemOptional.get();
        if (!item.getAvailable()) {
            throw new ValidationExceptionCustom("This item is currently unavailable.");
        }
        if (item.getOwner().getId().equals(bookerId)) {
            throw new EntityNotFoundException("You can't book your own item.");
        }

        Booking booking = BookingMapper.toBooking(bookingDto, booker, item);
        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));

    }

    @Override
    public BookingDtoOutput setApprove(Long ownerId, boolean approve, Long bookingId) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isEmpty()) {
            throw new EntityNotFoundException("Not found: booking id " + bookingId);
        }
        Booking booking = optionalBooking.get();

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new EntityNotFoundException("Booking must be approved by the item's owner.");
        }

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new ValidationExceptionCustom("Booking status must be 'WAITING'");
        }

        if (approve) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoOutput findById(Long bookingId, Long userId) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isEmpty()) {
            throw new EntityNotFoundException("Not found: booking id " + bookingId);
        }
        Booking booking = optionalBooking.get();
        Long ownerId = booking.getItem().getOwner().getId();
        Long bookerId = booking.getBooker().getId();
        if (userId.equals(ownerId) || userId.equals(bookerId)) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new EntityNotFoundException("Booking can be viewed only by item's owner or the booker.");
        }
    }

    @Override
    public List<BookingDtoOutput> findAll(Long userId, String state, Integer page, Integer size) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException("Not found: user " + userId);
        }
        BookingSearchState searchState;
        try {
            searchState = BookingSearchState.valueOf(state);
        } catch (IllegalArgumentException exception) {
            throw new ValidationExceptionCustom("Unknown state: " + state);
        }

        LocalDateTime now = LocalDateTime.now();
        Pageable params = PageRequest.of(page, size, Sort.by("start"));
        switch (searchState) {
            case ALL:
                return BookingMapper.toDtoList(bookingRepository.findAllBookingsByUserId(userId, params).toList());
            case CURRENT:
                return BookingMapper.toDtoList(bookingRepository.findAllBookingsByUserIdCurrent(userId, now, params).toList());
            case PAST:
                return BookingMapper.toDtoList(bookingRepository.findAllBookingsByUserIdPast(userId, now, params).toList());
            case FUTURE:
                return BookingMapper.toDtoList(bookingRepository.findAllBookingsByUserIdFuture(userId, now, params).toList());
            case WAITING:
                return BookingMapper.toDtoList(bookingRepository.findAllBookingsByUserIdWaiting(userId, params).toList());
            case REJECTED:
                return BookingMapper.toDtoList(bookingRepository.findAllBookingsByUserIdRejected(userId, params).toList());

            default:
                throw new ValidationExceptionCustom("Unknown state: " + state);
        }
    }

    @Override
    public List<BookingDtoOutput> findAllByOwner(Long ownerId, String state, Integer from, Integer size) {
        Optional<User> optionalUser = userRepository.findById(ownerId);
        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException("Not found: user " + ownerId);
        }

        LocalDateTime now = LocalDateTime.now();
        Pageable params = PageRequest.of(from, size, Sort.by("start"));
        switch (state) {
            case "ALL":
                return BookingMapper.toDtoList(bookingRepository.findAllBookingsByOwnerId(ownerId, params).toList());
            case "CURRENT":
                return BookingMapper.toDtoList(bookingRepository.findAllBookingsByOwnerIdCurrent(ownerId, now, params).toList());
            case "PAST":
                return BookingMapper.toDtoList(bookingRepository.findAllBookingsByOwnerIdPast(ownerId, now, params).toList());
            case "FUTURE":
                return BookingMapper.toDtoList(bookingRepository.findAllBookingsByOwnerIdFuture(ownerId, now, params).toList());
            case "WAITING":
                return BookingMapper.toDtoList(bookingRepository.findAllBookingsByOwnerIdWaiting(ownerId, params).toList());
            case "REJECTED":
                return BookingMapper.toDtoList(bookingRepository.findAllBookingsByOwnerIdRejected(ownerId, params).toList());

            default:
                throw new ValidationExceptionCustom("Unknown state: " + state);
        }
    }
}
