package ru.practicum.shareit.gateway.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.user.dto.UserDto;

import javax.validation.constraints.Positive;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserController {

    final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Validated(UserDto.Create.class) @RequestBody UserDto userDto) {
        log.debug("Request received: create user: {}", userDto);
        return userClient.create(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@Validated(UserDto.Update.class) @RequestBody UserDto userDto,
                                         @PathVariable("id") @Positive Long id) {
        userDto.setId(id);
        log.debug("Request received: update user: id: {}, {}", id, userDto);
        return userClient.update(userDto, id);
    }

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.debug("Request received: find all users.");
        return userClient.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findUserById(@PathVariable("id") @Positive Long id) {
        log.debug("Request received: find user by id: {}", id);
        return userClient.findUserById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> removeUser(@PathVariable @Positive Long id) {
        log.debug("Request received: remove user by id: {}", id);
        return userClient.removeUser(id);
    }
}
