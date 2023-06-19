package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
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
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemServiceImplTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    ItemMapper itemMapper;
    @Mock
    BookingMapper bookingMapper;
    @Mock
    CommentMapper commentMapper;

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
        bookingDtoForItemOwner.setBookerId(user.getId()); // or not "user"?

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

    }

    @Test
    public void createSuccessful() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto returnedItemDto = itemService.create(itemDto, owner.getId());
        assertNotNull(returnedItemDto);
        assertEquals(itemDto, returnedItemDto);
    }

    @Test
    public void createFailInCauseOfUserNotFound() {
        Long ownerId = 999L;
        doThrow(new EntityNotFoundException("Not found: user id: " + ownerId)).when(userRepository)
                .findById(ownerId);

        assertThatThrownBy(() -> itemService.create(itemDto, ownerId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Not found: user id: " + ownerId);
        verify(itemRepository, times(0)).save(any());
    }

    @Test
    public void updateSuccessful() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(updatedItem);

        ItemDto returnedItemDto = itemService.update(updatedItemDto, owner.getId());
        assertNotNull(returnedItemDto);
        assertEquals(updatedItemDto, returnedItemDto);
    }

    @Test
    public void updateFailInCauseOfUserNotFound() {
        Long ownerId = 999L;

        assertThatThrownBy(() -> itemService.update(updatedItemDto, ownerId))
                .isInstanceOf(EntityNotFoundException.class);
        verify(itemRepository, times(0)).save(any());
    }

    @Test
    public void findItemByIdSuccessful() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(anyLong())).thenReturn(new ArrayList<>());

        ItemDtoWithBookingsAndComments returnedItemDto = itemService.findItemById(user.getId(), item.getId());
        assertNotNull(returnedItemDto);
        assertEquals(itemBookingCommentDto, returnedItemDto);
    }

    @Test
    public void findOwnerItemsSuccessful() {
        Integer from = 0;
        Integer size = 4;

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findByOwnerId(anyLong(), any())).thenReturn(List.of(item));
        when(bookingRepository.findByItemIdList(anyList())).thenReturn(List.of(booking));

        List<ItemDtoWithBookingsAndComments> returnedItemDto = itemService.findAll(owner.getId(), from, size);

        assertNotNull(returnedItemDto.get(0));
        assertEquals(itemBookingCommentDto, returnedItemDto.get(0));
    }

    @Test
    public void searchSuccessful() {
        String text = "text";
        Integer from = 0;
        Integer size = 4;

        when(itemRepository.findItemsByTextIgnoreCase(anyString(), any())).thenReturn(List.of(item));

        List<ItemDto> returnedItemDto = itemService.search(text, from, size);
        assertNotNull(returnedItemDto.get(0));
        assertEquals(itemDto, returnedItemDto.get(0));
    }

    @Test
    public void addCommentSuccessful() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllCompletedBookingsByBookerIdAndItemId(anyLong(), anyLong(), any(LocalDateTime.class))).thenReturn(List.of(booking));
        when(commentRepository.save(any())).thenReturn(comment);

        CommentDtoOutput returnedCommentDto = itemService.createComment(user.getId(), item.getId(), commentDtoInput);
        assertNotNull(returnedCommentDto);
        assertEquals(commentDtoOutput, returnedCommentDto);
    }
}