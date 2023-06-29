package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        log.debug("Request received: create user: " + userDto);
        return userService.create(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@RequestBody UserDto userDto,
                          @PathVariable Long id) {
        userDto.setId(id);
        log.debug("Request received: update user: id: " + id + ", " + userDto);
        return userService.update(userDto);
    }

    @GetMapping
    public List<UserDto> findAll() {
        log.debug("Request received: find all users.");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDto findUserById(@PathVariable Long id) {
        log.debug("Request received: find user by id: " + id);
        return userService.findUserById(id);
    }

    @DeleteMapping("/{id}")
    public void removeUser(@PathVariable Long id) {
        log.debug("Request received: remove user by id: " + id);
        userService.removeUser(id);
    }
}
