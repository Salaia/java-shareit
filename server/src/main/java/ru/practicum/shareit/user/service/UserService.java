package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto);

    List<UserDto> findAll();

    UserDto findUserById(Long id);

    void removeUser(Long id);
}
