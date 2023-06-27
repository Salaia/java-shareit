package ru.practicum.shareit.request.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
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

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@FieldDefaults(level = AccessLevel.PRIVATE)
@SpringBootTest
public class ItemRequestServiceImplIntegrationTest {
    @Autowired
    ItemRequestService itemRequestServiceIntegration;

    @Autowired
    ItemRequestRepository itemRequestRepositoryIntegrated;
    @Autowired
    UserRepository userRepositoryIntegrated;
    @Autowired
    ItemRepository itemRepositoryIntegrated;

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
    public void findAllForRequesterIntegrated() {
        userRepositoryIntegrated.save(requesterUser);
        itemRequestRepositoryIntegrated.save(requestModel);

        List<ItemRequestDtoOutput> check = itemRequestServiceIntegration.findAllForRequester(
                requesterUser.getId(), 0, 1);
        assertEquals(check.get(0).getId(), requestDtoOutput.getId());
        assertEquals(check.get(0).getDescription(), requestDtoOutput.getDescription());
    }

    @Test
    public void findAllFromOthersIntegrated() {
        userRepositoryIntegrated.save(requesterUser);
        itemRequestRepositoryIntegrated.save(requestModel);

        List<ItemRequestDtoOutput> check = itemRequestServiceIntegration.findAllFromOthers(
                ownerDto.getId(), 0, 1);
        assertEquals(check.get(0).getId(), requestDtoOutput.getId());
        assertEquals(check.get(0).getDescription(), requestDtoOutput.getDescription());
    }

    @Test
    public void findByIdIntegrated() {
        userRepositoryIntegrated.save(requesterUser);
        itemRequestRepositoryIntegrated.save(requestModel);

        ItemRequestDtoOutput check = itemRequestServiceIntegration.findById(requesterUser.getId(),
                requestModel.getId());
        assertEquals(check.getId(), requestDtoOutput.getId());
        assertEquals(check.getDescription(), requestDtoOutput.getDescription());
    }
}
