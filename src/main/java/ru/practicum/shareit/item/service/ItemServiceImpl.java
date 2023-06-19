package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenOperationException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationExceptionCustom;
import ru.practicum.shareit.item.dto.CommentDtoInput;
import ru.practicum.shareit.item.dto.CommentDtoOutput;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsAndComments;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemServiceImpl implements ItemService {
    ItemRepository itemRepository;
    UserRepository userRepository;
    CommentRepository commentRepository;
    BookingRepository bookingRepository;

    ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto create(@Valid ItemDto itemDto, Long ownerId) {
        Optional<User> optionalOwner = userRepository.findById(ownerId);
        if (optionalOwner.isPresent()) {
            User owner = optionalOwner.get();
            ItemRequest request = null;
            if (itemDto.getRequestId() != null) {
                Optional<ItemRequest> optionalItemRequest = itemRequestRepository.findById(itemDto.getRequestId());
                if (optionalItemRequest.isEmpty()) {
                    throw new EntityNotFoundException("Not found: request " + itemDto.getRequestId());
                }
                request = optionalItemRequest.get();
            }
            Item item = ItemMapper.toItem(itemDto, owner, request);
            return ItemMapper.toItemDto(itemRepository.save(item));
        } else {
            throw new EntityNotFoundException("Not found: item owner's id " + ownerId);
        }
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long ownerId) {
        Optional<Item> optionalItem = itemRepository.findById(itemDto.getId());
        if (optionalItem.isPresent()) {
            Optional<User> optionalOwner = userRepository.findById(ownerId);
            if (optionalOwner.isEmpty()) {
                throw new EntityNotFoundException("Not found: owner id: " + ownerId);
            }
            Item item = optionalItem.get();
            User owner = optionalOwner.get();

            if (!ownerId.equals(item.getOwner().getId())) {
                throw new ForbiddenOperationException("Item can be changed only by it's owner!");
            }
            if (itemDto.getName() != null &&
                    !itemDto.getName().equals(item.getName())) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null &&
                    !itemDto.getDescription().equals(item.getDescription())) {
                item.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null &&
                    !itemDto.getAvailable().equals(item.getAvailable())) {
                item.setAvailable(itemDto.getAvailable());
            }

            return ItemMapper.toItemDto(itemRepository.save(item));
        } else {
            throw new EntityNotFoundException("Not found: item id " + itemDto.getId());
        }
    }

    @Override
    public ItemDtoWithBookingsAndComments findItemById(Long itemId, Long userId) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            throw new EntityNotFoundException("Not found: item id " + itemId);
        }
        Item item = optionalItem.get();
        Booking lastBooking = null;
        Booking nextBooking = null;
        if (item.getOwner().getId().equals(userId)) {
            List<Booking> bookings = bookingRepository.findByItemId(itemId);
            if (!bookings.isEmpty()) {
                List<Booking> lastList = bookings.stream()
                        .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                        .sorted(Comparator.comparing(Booking::getEnd).reversed())
                        .collect(Collectors.toList());
                if (!lastList.isEmpty()) {
                    lastBooking = lastList.get(0);
                }
                List<Booking> nextList = bookings.stream()
                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                        .sorted(Comparator.comparing(Booking::getStart))
                        .collect(Collectors.toList());
                if (!nextList.isEmpty()) {
                    nextBooking = nextList.get(0);
                }
            }
        }
        List<Comment> comments = commentRepository.findByItemId(item.getId());

        return ItemMapper.toDtoExtended(item, lastBooking, nextBooking, comments);
    }

    public List<ItemDtoWithBookingsAndComments> findAll(Long ownerId, Integer from, Integer size) {
        Optional<User> optionalUser = userRepository.findById(ownerId);
        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException("Not found: owner id " + ownerId);
        }
        int page = from / size;
        Pageable params = PageRequest.of(page, size, Sort.by("id"));
        List<Item> items = itemRepository.findByOwnerId(ownerId, params);
        if (items.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Booking> allBookings = bookingRepository.findByItemIdList(itemIds);
        List<Comment> allComments = commentRepository.findByItemIdList(itemIds);
        List<ItemDtoWithBookingsAndComments> result = new ArrayList<>();
        for (Item item : items) {
            Booking lastBooking = null;
            Booking nextBooking = null;
            List<Booking> thisBookings = allBookings.stream()
                    .filter(booking -> booking.getItem().equals(item))
                    .collect(Collectors.toList());
            if (!thisBookings.isEmpty()) {
                List<Booking> lastList = thisBookings.stream()
                        .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                        .sorted(Comparator.comparing(Booking::getEnd).reversed())
                        .collect(Collectors.toList());
                if (!lastList.isEmpty()) {
                    lastBooking = lastList.get(0);
                }
                List<Booking> nextList = thisBookings.stream()
                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                        .sorted(Comparator.comparing(Booking::getStart))
                        .collect(Collectors.toList());
                if (!nextList.isEmpty()) {
                    nextBooking = nextList.get(0);
                }
            }

            List<Comment> thisComments = allComments.stream()
                    .filter(comment -> comment.getItem().equals(item))
                    .collect(Collectors.toList());

            result.add(ItemMapper.toDtoExtended(item, lastBooking, nextBooking, thisComments));
        }
        return result;

    }

    @Override
    public List<ItemDto> search(String text, Integer from, Integer size) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        int page = from / size;
        Pageable params = PageRequest.of(page, size, Sort.by("id"));
        List<Item> items = itemRepository.findItemsByTextIgnoreCase(text, params);
        return ItemMapper.itemDtoList(items);
    }

    @Override
    public CommentDtoOutput createComment(Long itemId, Long bookerId, CommentDtoInput commentDto) {
        if (commentDto.getText().isBlank()) {
            throw new ValidationExceptionCustom("Comment can't be empty!");
        }
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if ((optionalItem.isEmpty())) {
            throw new EntityNotFoundException("Not found: item " + itemId);
        }
        Item item = optionalItem.get();
        List<Booking> bookings = bookingRepository.findAllCompletedBookingsByBookerIdAndItemId(bookerId, itemId, LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new ValidationExceptionCustom("Comments are only allowed after at least one booking is done.");
        }
        Optional<User> optionalUser = userRepository.findById(bookerId);
        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException("Not found: user " + bookerId);
        }
        User booker = optionalUser.get();

        Comment comment = commentRepository.save(CommentMapper.toComment(commentDto, booker, item));
        return CommentMapper.toCommentDto(comment);
    }
}
