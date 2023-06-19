package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationExceptionCustom;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.*;


@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingServiceImplTest {

    @InjectMocks
    BookingServiceImpl bookingService;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;


    final LocalDateTime start = LocalDateTime.of(3033, Month.JANUARY, 9, 17, 10, 11);
    final LocalDateTime end = LocalDateTime.of(3033, Month.JANUARY, 9, 17, 40, 11);
    final UserDto owner = UserDto.builder()
            .id(1L)
            .name("owner")
            .email("owner.user@mail.com")
            .build();
    final UserDto booker = UserDto.builder()
            .id(2L)
            .name("booker")
            .email("booker.user@mail.com")
            .build();

    final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Item name")
            .description("Item description")
            .available(true)
            .build();

    final BookingDtoOutput bookingDtoOutput = BookingDtoOutput.builder()
            .id(1L)
            .item(itemDto)
            .start(start)
            .end(end)
            .booker(booker)
            .status(BookingStatus.WAITING)
            .build();

    BookingDtoInput bookingDtoInput;
    final User userBooker = new User();
    final User userOwner = new User();
    final Item item = new Item();
    final Booking booking = new Booking();
    static Optional<User> optionalUserBooker;
    static Optional<User> optionalUserOwner;
    static Optional<Item> optionalItem;
    static Optional<Booking> optionalBooking;
    List<Booking> bookingPage;

    @BeforeEach
    public void setUp() {
        userBooker.setId(booker.getId());
        userBooker.setName(booker.getName());
        userBooker.setEmail(booker.getEmail());
        optionalUserBooker = Optional.of(userBooker);

        userOwner.setId(owner.getId());
        userOwner.setName(owner.getName());
        userOwner.setEmail(owner.getEmail());
        optionalUserOwner = Optional.of(userOwner);

        item.setId(1L);
        item.setName("Item name");
        item.setDescription("Item description");
        item.setOwner(userOwner);
        item.setAvailable(true);
        optionalItem = Optional.of(item);

        booking.setId(1L);
        booking.setBooker(userBooker);
        booking.setItem(item);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setStatus(BookingStatus.WAITING);
        optionalBooking = Optional.of(booking);

        bookingDtoInput = BookingDtoInput.builder()
                .bookerId(booker.getId())
                .itemId(itemDto.getId())
                .start(start)
                .end(end)
                .build();

        bookingPage = List.of(booking, booking, booking);
    }

    @Test
    public void createBookingSuccess() {
        when(bookingRepository.save(any())).thenReturn(booking);
        when(userRepository.findById(booker.getId())).thenReturn(optionalUserBooker);
        when(itemRepository.findById(anyLong())).thenReturn(optionalItem);

        BookingDtoOutput checkDto = bookingService.create(bookingDtoInput, booker.getId());
        assertEquals(checkDto, bookingDtoOutput);
    }

    @Test
    public void failToBookOwnerOrBookerNotFound() {

        when(userRepository.findById(anyLong())).thenThrow(new EntityNotFoundException("Not Found"));

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.create(bookingDtoInput, anyLong()));
    }

    @Test
    public void failToBookItemNotFound() {
        when(itemRepository.findById(anyLong())).thenThrow(new EntityNotFoundException("Not Found"));
        when(userRepository.findById(anyLong())).thenReturn(optionalUserBooker);

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.create(bookingDtoInput, anyLong()));
    }

    @Test
    public void failToBookYourOwnItem() {
        when(itemRepository.findById(anyLong())).thenReturn(optionalItem);

        bookingDtoInput.setBookerId(owner.getId());
        when(userRepository.findById(owner.getId())).thenReturn(optionalUserOwner);

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.create(bookingDtoInput, owner.getId()));
    }

    @Test
    public void failToBookItemNotAvailable() {
        item.setAvailable(false);
        optionalItem = Optional.of(item);
        when(itemRepository.findById(anyLong())).thenReturn(optionalItem);
        when(userRepository.findById(anyLong())).thenReturn(optionalUserBooker);

        assertThrows(ValidationExceptionCustom.class,
                () -> bookingService.create(bookingDtoInput, owner.getId()));
    }

    @Test
    public void failToBookStartAfterEnd() {
        bookingDtoInput.setEnd(LocalDateTime.now());

        assertThrows(ValidationExceptionCustom.class,
                () -> bookingService.create(bookingDtoInput, owner.getId()));
    }

    @Test
    public void failToBookStartEqualsEnd() {
        bookingDtoInput.setEnd(start);

        assertThrows(ValidationExceptionCustom.class,
                () -> bookingService.create(bookingDtoInput, owner.getId()));
    }

    @Test
    public void setApproveSuccess() {
        when(bookingRepository.findById(anyLong())).thenReturn(optionalBooking);
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDtoOutput output = bookingService.setApprove(owner.getId(), true, booking.getId());
        assertEquals(BookingStatus.APPROVED, output.getStatus());
    }

    @Test
    public void setRejectedSuccess() {
        when(bookingRepository.findById(anyLong())).thenReturn(optionalBooking);
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDtoOutput output = bookingService.setApprove(owner.getId(), false, booking.getId());
        assertEquals(BookingStatus.REJECTED, output.getStatus());
    }

    @Test
    public void failSetApproveBecauseStatusWasNotWaiting() {
        booking.setStatus(BookingStatus.APPROVED);
        optionalBooking = Optional.of(booking);
        when(bookingRepository.findById(anyLong())).thenReturn(optionalBooking);

        assertThrows(ValidationExceptionCustom.class,
                () -> bookingService.setApprove(owner.getId(), false, booking.getId()));
    }

    @Test
    public void failSetApproveBecauseUserIsNotOwner() {
        when(bookingRepository.findById(anyLong())).thenReturn(optionalBooking);

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.setApprove(999L, false, booking.getId()));
    }

    @Test
    public void failSetApproveBookingNotFound() {
        when(bookingRepository.findById(anyLong())).thenThrow(new EntityNotFoundException("Not Found"));

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.setApprove(owner.getId(), false, booking.getId()));
    }

    @Test
    public void findBookingByIdSuccess() {
        when(bookingRepository.findById(anyLong())).thenReturn(optionalBooking);

        bookingService.findById(booking.getId(), owner.getId());
    }

    @Test
    public void failFindByIdByWrongUser() {
        when(bookingRepository.findById(anyLong())).thenReturn(optionalBooking);

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.findById(booking.getId(), -1L));

    }

    @Test
    public void failFindByIdBookingNotFound() {
        when(bookingRepository.findById(anyLong())).thenThrow(new EntityNotFoundException("Not Found"));

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.findById(booking.getId(), owner.getId()));

    }

    @Test
    public void findAllForBookerAllSuccess() {
        when(userRepository.findById(booker.getId())).thenReturn(optionalUserBooker);
        when(bookingRepository.findAllBookingsByUserId(anyLong(), any())).thenReturn(bookingPage);

        List<BookingDtoOutput> output = bookingService.findAll(booker.getId(), "ALL", 0, 3);
        assertEquals(output.size(), 3);
    }

    @Test
    public void findAllForBookerCURRENTSuccess() {
        when(userRepository.findById(booker.getId())).thenReturn(optionalUserBooker);
        when(bookingRepository.findAllBookingsByUserIdCurrent(anyLong(), any(), any())).thenReturn(bookingPage);

        List<BookingDtoOutput> output = bookingService.findAll(booker.getId(), "CURRENT", 0, 3);
        assertEquals(output.size(), 3);
    }

    @Test
    public void findAllForBookerPASTSuccess() {
        when(userRepository.findById(booker.getId())).thenReturn(optionalUserBooker);
        when(bookingRepository.findAllBookingsByUserIdPast(anyLong(), any(), any())).thenReturn(bookingPage);

        List<BookingDtoOutput> output = bookingService.findAll(booker.getId(), "PAST", 0, 3);
        assertEquals(output.size(), 3);
    }

    @Test
    public void findAllForBookerFUTURESuccess() {
        when(userRepository.findById(booker.getId())).thenReturn(optionalUserBooker);
        when(bookingRepository.findAllBookingsByUserIdFuture(anyLong(), any(), any())).thenReturn(bookingPage);

        List<BookingDtoOutput> output = bookingService.findAll(booker.getId(), "FUTURE", 0, 3);
        assertEquals(output.size(), 3);
    }

    @Test
    public void findAllForBookerWAITINGSuccess() {
        when(userRepository.findById(booker.getId())).thenReturn(optionalUserBooker);
        when(bookingRepository.findAllBookingsByUserIdWaiting(anyLong(), any())).thenReturn(bookingPage);

        List<BookingDtoOutput> output = bookingService.findAll(booker.getId(), "WAITING", 0, 3);
        assertEquals(output.size(), 3);
    }

    @Test
    public void findAllForBookerREJECTEDSuccess() {
        when(userRepository.findById(booker.getId())).thenReturn(optionalUserBooker);
        when(bookingRepository.findAllBookingsByUserIdRejected(anyLong(), any())).thenReturn(bookingPage);

        List<BookingDtoOutput> output = bookingService.findAll(booker.getId(), "REJECTED", 0, 3);
        assertEquals(output.size(), 3);
    }

    @Test
    public void failFindAllForBookerUnknownState() {
        when(userRepository.findById(booker.getId())).thenReturn(optionalUserBooker);

        assertThrows(ValidationExceptionCustom.class,
                () -> bookingService.findAll(booker.getId(), "MEOW", 0, 3));
    }

    @Test
    public void failFindAllForOwnerUnknownState() {
        when(userRepository.findById(owner.getId())).thenReturn(optionalUserBooker);

        assertThrows(ValidationExceptionCustom.class,
                () -> bookingService.findAllByOwner(owner.getId(), "MEOW", 0, 3));
    }

    @Test
    public void findAllForOwnerALLSuccess() {
        when(userRepository.findById(owner.getId())).thenReturn(optionalUserOwner);
        when(bookingRepository.findAllBookingsByOwnerId(anyLong(), any())).thenReturn(bookingPage);

        List<BookingDtoOutput> output = bookingService.findAllByOwner(owner.getId(), "ALL", 0, 3);
        assertEquals(output.size(), 3);
    }

    @Test
    public void findAllForOwnerCURRENTSuccess() {
        when(userRepository.findById(owner.getId())).thenReturn(optionalUserOwner);
        when(bookingRepository.findAllBookingsByOwnerIdCurrent(anyLong(), any(), any())).thenReturn(bookingPage);

        List<BookingDtoOutput> output = bookingService.findAllByOwner(owner.getId(), "CURRENT", 0, 3);
        assertEquals(output.size(), 3);
    }

    @Test
    public void findAllForOwnerPASTSuccess() {
        when(userRepository.findById(owner.getId())).thenReturn(optionalUserOwner);
        when(bookingRepository.findAllBookingsByOwnerIdPast(anyLong(), any(), any())).thenReturn(bookingPage);

        List<BookingDtoOutput> output = bookingService.findAllByOwner(owner.getId(), "PAST", 0, 3);
        assertEquals(output.size(), 3);
    }

    @Test
    public void findAllForOwnerFUTURESuccess() {
        when(userRepository.findById(owner.getId())).thenReturn(optionalUserOwner);
        when(bookingRepository.findAllBookingsByOwnerIdFuture(anyLong(), any(), any())).thenReturn(bookingPage);

        List<BookingDtoOutput> output = bookingService.findAllByOwner(owner.getId(), "FUTURE", 0, 3);
        assertEquals(output.size(), 3);
    }

    @Test
    public void findAllForOwnerWAITINGSuccess() {
        when(userRepository.findById(owner.getId())).thenReturn(optionalUserOwner);
        when(bookingRepository.findAllBookingsByOwnerIdWaiting(anyLong(), any())).thenReturn(bookingPage);

        List<BookingDtoOutput> output = bookingService.findAllByOwner(owner.getId(), "WAITING", 0, 3);
        assertEquals(output.size(), 3);
    }

    @Test
    public void findAllForOwnerREJECTEDSuccess() {
        when(userRepository.findById(owner.getId())).thenReturn(optionalUserOwner);
        when(bookingRepository.findAllBookingsByOwnerIdRejected(anyLong(), any())).thenReturn(bookingPage);

        List<BookingDtoOutput> output = bookingService.findAllByOwner(owner.getId(), "REJECTED", 0, 3);
        assertEquals(output.size(), 3);
    }
}