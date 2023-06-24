package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDtoInput;
import ru.practicum.shareit.item.dto.CommentDtoOutput;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsAndComments;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@FieldDefaults(level = AccessLevel.PRIVATE)
@SpringBootTest
public class ItemServiceImplIntegrationTest {
    @Autowired
    ItemService itemServiceIntegration;
    @Autowired
    ItemRepository itemRepositoryIntegrated;
    @Autowired
    UserRepository userRepositoryIntegrated;
    @Autowired
    BookingRepository bookingRepositoryIntegrated;
    @Autowired
    CommentRepository commentRepositoryIntegrated;
    @Autowired
    ItemRequestRepository itemRequestRepositoryIntegrated;

    static User owner;
    static User user;
    static ItemDto itemDto;
    static ItemDto updatedItemDto;
    static Item item;
    static Item updatedItem;
    static BookingDtoShort bookingDtoForItemOwner;
    static ItemDtoWithBookingsAndComments itemBookingCommentDto;
    static Booking booking;

    static CommentDtoInput commentDtoInput;
    static Comment comment;
    static CommentDtoOutput commentDtoOutput;
    static ItemRequest itemRequest;
    static ItemDto itemDtoForRequest;
    static Item itemForRequest;

    @BeforeEach
    public void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("owner@mail.com");

        user = new User();
        user.setId(2L);
        user.setName("User");
        user.setEmail("user@mail.com");

        itemDto = ItemDto.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .build();

        item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(owner);

        updatedItemDto = ItemDto.builder()
                .id(1L)
                .name("UpdatedItem")
                .description("UpdatedDescription")
                .available(false)
                .build();

        updatedItem = new Item();
        updatedItem.setId(updatedItemDto.getId());
        updatedItem.setName(updatedItemDto.getName());
        updatedItem.setDescription(updatedItemDto.getDescription());
        updatedItem.setAvailable(updatedItemDto.getAvailable());
        updatedItem.setOwner(owner);

        bookingDtoForItemOwner = new BookingDtoShort();
        bookingDtoForItemOwner.setId(1L);
        bookingDtoForItemOwner.setBookerId(user.getId());

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(3));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.APPROVED);

        itemBookingCommentDto = new ItemDtoWithBookingsAndComments();
        itemBookingCommentDto.setId(1L);
        itemBookingCommentDto.setName("Item");
        itemBookingCommentDto.setDescription("Description");
        itemBookingCommentDto.setAvailable(true);
        itemBookingCommentDto.setLastBooking(bookingDtoForItemOwner);
        itemBookingCommentDto.setNextBooking(bookingDtoForItemOwner);
        itemBookingCommentDto.setComments(new ArrayList<>());

        commentDtoInput = new CommentDtoInput("comment");

        comment = new Comment();
        comment.setId(1L);
        comment.setText(commentDtoInput.getText());
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(Instant.now());

        commentDtoOutput = new CommentDtoOutput();
        commentDtoOutput.setId(1L);
        commentDtoOutput.setText(commentDtoInput.getText());
        commentDtoOutput.setAuthorName(user.getName());
        commentDtoOutput.setCreated(comment.getCreated());

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setRequester(user);
        itemRequest.setDescription("Request description");
        itemRequest.setCreated(LocalDateTime.now());

        itemDtoForRequest = ItemDto.builder()
                .id(2L)
                .name("Item for request name")
                .description("Describe item for request")
                .available(true)
                .requestId(1L)
                .build();

        itemForRequest = new Item();
        itemForRequest.setId(itemDtoForRequest.getId());
        itemForRequest.setName(itemDtoForRequest.getName());
        itemForRequest.setDescription(itemDtoForRequest.getDescription());
        itemForRequest.setOwner(owner);
        itemForRequest.setRequest(itemRequest);
        itemForRequest.setAvailable(itemDtoForRequest.getAvailable());

    }

    @Test
    public void findItemByIdIntegrated() {
        User ownerOpt = userRepositoryIntegrated.save(owner);
        User bookerOpt = userRepositoryIntegrated.save(user);
        Item itemSaved = itemRepositoryIntegrated.save(item);

        ItemDtoWithBookingsAndComments check = itemServiceIntegration.findItemById(item.getId(), owner.getId());
        assertEquals(check.getId(), itemBookingCommentDto.getId());
        assertEquals(check.getName(), itemBookingCommentDto.getName());
        assertEquals(check.getDescription(), itemBookingCommentDto.getDescription());
    }

    @Test
    public void findAllIntegrated() {
        User ownerOpt = userRepositoryIntegrated.save(owner);
        User bookerOpt = userRepositoryIntegrated.save(user);
        Item itemSaved = itemRepositoryIntegrated.save(item);

        List<ItemDtoWithBookingsAndComments> check = itemServiceIntegration.findAll(owner.getId(), 0, 1);
        assertEquals(check.get(0).getId(), itemBookingCommentDto.getId());
        assertEquals(check.get(0).getName(), itemBookingCommentDto.getName());
        assertEquals(check.get(0).getDescription(), itemBookingCommentDto.getDescription());
    }

    @Test
    public void searchIntegrated() {
        User ownerOpt = userRepositoryIntegrated.save(owner);
        User bookerOpt = userRepositoryIntegrated.save(user);
        Item itemSaved = itemRepositoryIntegrated.save(item);

        List<ItemDto> check = itemServiceIntegration.search("itEm", 0, 1);
        assertEquals(check.get(0).getId(), itemDto.getId());
        assertEquals(check.get(0).getName(), itemDto.getName());
        assertEquals(check.get(0).getDescription(), itemDto.getDescription());
    }
}
