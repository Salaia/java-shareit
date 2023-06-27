package ru.practicum.shareit.user.service;

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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

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
class UserServiceImplTest {
    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    static UserDto userDto;

    final User user = new User();
    static Optional<User> optionalUser;
    static List<User> userList;

    @BeforeEach
    public void init() {
        userDto = UserDto.builder()
                .id(1L)
                .name("User name")
                .email("test.user@mail.com")
                .build();

        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

        optionalUser = Optional.of(user);

        userList = List.of(user, user, user);
    }

    @Test
    public void createUserSuccess() {
        when(userRepository.save(any()))
                .thenReturn(user);

        UserDto dtoReturned = userService.create(userDto);

        assertEquals(userDto.getId(), dtoReturned.getId());
        assertEquals(userDto.getName(), dtoReturned.getName());
        assertEquals(userDto.getEmail(), dtoReturned.getEmail());
    }

    @Test
    public void updateUserSuccess() {
        when(userRepository.findById(anyLong())).thenReturn(optionalUser);
        when(userRepository.save(any())).thenReturn(user);

        userDto.setName("Update");
        UserDto output = userService.update(userDto);
        assertEquals(output.getName(), userDto.getName());
    }

    @Test
    public void failUpdateUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.update(userDto));
    }

    @Test
    public void findAllSuccess() {
        when(userRepository.findAll()).thenReturn(userList);

        List<UserDto> output = userService.findAll();
        assertEquals(output.size(), 3);
    }

    @Test
    public void findUserByIdSuccess() {
        when(userRepository.findById(anyLong())).thenReturn(optionalUser);

        UserDto output = userService.findUserById(userDto.getId());
        assertEquals(userDto.getName(), output.getName());
    }

    @Test
    public void failFindUserByIdNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.findUserById(999L));
    }

    @Test
    public void failRemoveUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.removeUser(999L));
    }

    @Test
    public void removeUserSuccess() {
        when(userRepository.findById(anyLong())).thenReturn(optionalUser);
        userService.removeUser(1L);
    }
}