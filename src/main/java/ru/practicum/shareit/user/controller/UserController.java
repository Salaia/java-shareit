package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Validated
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto create(@Validated(UserDto.Create.class) @RequestBody UserDto userDto) {
        log.debug("Request received: create user.");
        return userService.create(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@Validated(UserDto.Update.class) @RequestBody UserDto userDto,
                          @PathVariable("id") @Positive Long id) {
        userDto.setId(id);
        log.debug("Request received: update user.");
        return userService.update(userDto);
    }

    @GetMapping
    public List<UserDto> findAll() {
        log.debug("Request received: find all users.");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDto findUserById(@PathVariable("id") @Positive Long id) {
        log.debug("Request received: find user by id.");
        return userService.findUserById(id);
    }

    @DeleteMapping("/{id}")
    public void removeUser(@PathVariable @Positive Long id) {
        log.debug("Request received: remove user by id.");
        userService.removeUser(id);
    }
}
