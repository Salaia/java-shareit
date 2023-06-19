package ru.practicum.shareit.request.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutput;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRequestServiceImplTest {

    @InjectMocks
    ItemRequestServiceImpl itemRequestService;
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;

    static UserDto requesterDto;
    static User requesterUser;
    static Optional<User> requesterUserOpt;
    static UserDto ownerDto;
    static ItemRequestDtoInput requestDtoInput;
    static ItemRequestDtoOutput requestDtoOutput;
    static ItemRequest requestModel;

    @BeforeEach
    public void setUp() {
        requesterDto = UserDto.builder()
                .id(1L)
                .name("requester")
                .email("requester.user@mail.com")
                .build();

        requesterUser = new User();
        requesterUser.setId(requesterDto.getId());
        requesterUser.setName(requesterDto.getName());
        requesterUser.setEmail(requesterDto.getEmail());

        requesterUserOpt = Optional.of(requesterUser);

        ownerDto = UserDto.builder()
                .id(2L)
                .name("owner")
                .email("owner.user@mail.com")
                .build();

        requestDtoInput = ItemRequestDtoInput.builder()
                .description("Request").build();

        requestDtoOutput = ItemRequestDtoOutput.builder()
                .id(1L)
                .description(requestDtoInput.getDescription())
                .created(LocalDateTime.now())
                .build();

        requestModel = new ItemRequest();
        requestModel.setId(requestDtoOutput.getId());
        requestModel.setRequester(requesterUser);
        requestModel.setCreated(LocalDateTime.now());
        requestModel.setDescription(requestDtoInput.getDescription());
    }

    @Test
    public void createRequestSuccess() {
        when(userRepository.findById(anyLong())).thenReturn(requesterUserOpt);
        when(itemRequestRepository.save(any())).thenReturn(requestModel);

        ItemRequestDtoOutput output = itemRequestService.create(requesterDto.getId(), requestDtoInput);
        assertEquals(output.getDescription(), requestDtoOutput.getDescription());
    }

    @Test
    public void failCreateRequesterNotFound() {
        when(userRepository.findById(anyLong())).thenThrow(new EntityNotFoundException("Not Found"));
        assertThrows(EntityNotFoundException.class,
                () -> itemRequestService.create(requesterDto.getId(), requestDtoInput));
    }

    @Test
    public void findAllForRequesterSuccess() {
        when(userRepository.findById(anyLong())).thenReturn(requesterUserOpt);
//TODO continue all this ****
        itemRequestService.findAllForRequester(requesterDto.getId(), 0, 3);

    }

}

/*
        when(userRepository.findById(anyLong())).thenThrow(new EntityNotFoundException("Not Found"));

        assertThrows(EntityNotFoundException.class,
                () -> userService.update(userDto));

 */