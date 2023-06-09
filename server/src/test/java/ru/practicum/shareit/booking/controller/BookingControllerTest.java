package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ForbiddenOperationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    final UserDto owner = UserDto.builder()
            .id(1L)
            .name("Owner")
            .email("owner.user@mail.com")
            .build();

    final UserDto booker = UserDto.builder()
            .id(2L)
            .name("Booker")
            .email("booker.user@mail.com")
            .build();

    final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Item name")
            .description("Item description")
            .available(false)
            .build();

    final LocalDateTime start = LocalDateTime.of(3023, Month.JUNE, 16, 14, 1, 1);
    final LocalDateTime end = LocalDateTime.of(3023, Month.JUNE, 16, 14, 30, 1);
    final BookingDtoInput bookingDtoInput = BookingDtoInput.builder()
            .bookerId(booker.getId())
            .itemId(itemDto.getId())
            .start(start)
            .end(end)
            .build();

    final BookingDtoOutput bookingDtoOutput = BookingDtoOutput.builder()
            .id(1L)
            .booker(booker)
            .item(itemDto)
            .start(start)
            .end(end)
            .status(BookingStatus.WAITING)
            .build();

    @Test
    public void createBookingSuccess() throws Exception {
        when(bookingService.create(any(), anyLong())).thenReturn(bookingDtoOutput);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", booker.getId())
                        .content(mapper.writeValueAsString(bookingDtoInput))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOutput.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoOutput.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDtoOutput.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(bookingDtoOutput.getStatus().toString())))
                .andExpect(jsonPath("$.item.id", is(bookingDtoOutput.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDtoOutput.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoOutput.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(bookingDtoOutput.getBooker().getName())));
    }

    @Test
    public void failCreateBookingBookerNotFound() throws Exception {
        when(bookingService.create(any(), anyLong())).thenThrow(new EntityNotFoundException("Not Found"));
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 999)
                        .content(mapper.writeValueAsString(bookingDtoInput))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void findBookingByIdSuccess() throws Exception {
        when(bookingService.findById(any(), anyLong())).thenReturn(bookingDtoOutput);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", booker.getId())
                        .content(mapper.writeValueAsString(bookingDtoInput))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOutput.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoOutput.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDtoOutput.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(bookingDtoOutput.getStatus().toString())))
                .andExpect(jsonPath("$.item.id", is(bookingDtoOutput.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDtoOutput.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoOutput.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(bookingDtoOutput.getBooker().getName())));
    }

    @Test
    public void failFindByIdBookingNotFound() throws Exception {
        when(bookingService.findById(any(), anyLong())).thenThrow(new EntityNotFoundException("Not Found"));
        mockMvc.perform(get("/bookings/999")
                        .header("X-Sharer-User-Id", booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void setBookingStatusApprovedSuccess() throws Exception {
        bookingDtoOutput.setStatus(BookingStatus.APPROVED);
        when(bookingService.setApprove(any(), anyBoolean(), anyLong())).thenReturn(bookingDtoOutput);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", owner.getId())
                        .content(mapper.writeValueAsString(bookingDtoInput))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(bookingDtoOutput.getStatus().toString())));
    }

    @Test
    public void setBookingStatusRejectedSuccess() throws Exception {
        bookingDtoOutput.setStatus(BookingStatus.REJECTED);
        when(bookingService.setApprove(any(), anyBoolean(), anyLong())).thenReturn(bookingDtoOutput);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", owner.getId())
                        .content(mapper.writeValueAsString(bookingDtoInput))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(bookingDtoOutput.getStatus().toString())));
    }

    @Test
    public void failToSetStatusWrongUser() throws Exception {
        when(bookingService.setApprove(any(), anyBoolean(), anyLong())).thenThrow(new ForbiddenOperationException("Wrong user"));
        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void failToSetStatusWrongStatusNotWaiting() throws Exception {
        when(bookingService.setApprove(any(), anyBoolean(), anyLong())).thenThrow(
                new ForbiddenOperationException("Booking status must be 'WAITING'"));
        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void findAllForBookerSuccess() throws Exception {
        List<BookingDtoOutput> dtoOutputList = List.of(bookingDtoOutput, bookingDtoOutput);
        when(bookingService.findAll(anyLong(), any(), anyInt(), anyInt())).thenReturn(dtoOutputList);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", booker.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

    }

    @Test
    public void findAllForOwnerSuccess() throws Exception {
        List<BookingDtoOutput> dtoOutputList = List.of(bookingDtoOutput, bookingDtoOutput);
        when(bookingService.findAllByOwner(anyLong(), any(), anyInt(), anyInt())).thenReturn(dtoOutputList);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", owner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

    }

}