package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@FieldDefaults(level = AccessLevel.PRIVATE)
@SpringBootTest
public class BookingServiceImplIntegrationTest {
    @Autowired
    BookingService bookingServiceIntegration;
    @Autowired
    BookingRepository bookingRepositoryIntegration;
    @Autowired
    UserRepository userRepositoryIntegration;
    @Autowired
    ItemRepository itemRepositoryIntegration;

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
    public void findByIdIntegrated() {
        userRepositoryIntegration.save(userOwner);
        userRepositoryIntegration.save(userBooker);
        itemRepositoryIntegration.save(item);
        bookingRepositoryIntegration.save(booking);

        BookingDtoOutput check = bookingServiceIntegration.findById(booking.getId(), booker.getId());
        assertEquals(check.getId(), bookingDtoOutput.getId());
        assertEquals(check.getBooker(), bookingDtoOutput.getBooker());
        assertEquals(check.getItem(), bookingDtoOutput.getItem());
    }

    @Test
    public void findAllIntegrated() {
        userRepositoryIntegration.save(userOwner);
        userRepositoryIntegration.save(userBooker);
        itemRepositoryIntegration.save(item);
        bookingRepositoryIntegration.save(booking);

        List<BookingDtoOutput> check = bookingServiceIntegration.findAll(booker.getId(), "ALL", 0, 1);
        assertEquals(check.get(0).getId(), bookingDtoOutput.getId());
        assertEquals(check.get(0).getBooker(), bookingDtoOutput.getBooker());
        assertEquals(check.get(0).getItem(), bookingDtoOutput.getItem());
    }

    @Test
    public void findAllByOwnerIntegrated() {
        userRepositoryIntegration.save(userOwner);
        userRepositoryIntegration.save(userBooker);
        itemRepositoryIntegration.save(item);
        bookingRepositoryIntegration.save(booking);

        List<BookingDtoOutput> check = bookingServiceIntegration.findAllByOwner(owner.getId(), "ALL", 0, 1);
        assertEquals(check.get(0).getId(), bookingDtoOutput.getId());
        assertEquals(check.get(0).getBooker(), bookingDtoOutput.getBooker());
        assertEquals(check.get(0).getItem(), bookingDtoOutput.getItem());
    }
}
