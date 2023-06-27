package ru.practicum.shareit.request.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    static Pageable params = PageRequest.of(0, 1, Sort.by("created"));

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
                .items(new ArrayList<>())
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
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> itemRequestService.create(requesterDto.getId(), requestDtoInput));
    }

    @Test
    public void findAllForRequesterSuccess() {
        when(userRepository.findById(anyLong())).thenReturn(requesterUserOpt);
        itemRequestService.findAllForRequester(requesterDto.getId(), 0, 3);

    }

    @Test
    public void failFindByIdUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> itemRequestService.findById(requesterDto.getId(), requestModel.getId()));
    }

    @Test
    public void failFindByIdRequestNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requesterUser));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> itemRequestService.findById(requesterDto.getId(), requestModel.getId()));
    }

    @Test
    public void findRequestByIdSuccess() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requesterUser));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(requestModel));
        ItemRequestDtoOutput check = itemRequestService.findById(requesterUser.getId(), requestModel.getId());
        assertEquals(requestDtoOutput, check);
    }

    @Test
    public void findAllFromOthersSuccess() {
        when(itemRequestRepository.findAllFromOthers(anyLong(), any())).thenReturn(List.of(requestModel));
        List<ItemRequestDtoOutput> result = itemRequestService.findAllFromOthers(requesterDto.getId(), 0, 1);
        assertEquals(requestDtoOutput, result.get(0));
    }

}