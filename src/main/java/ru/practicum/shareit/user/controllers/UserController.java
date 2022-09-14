package ru.practicum.shareit.user.controllers;

import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.UserNotFound;
import ru.practicum.shareit.user.services.UserService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto save(@RequestBody UserDto userDto) {
        if (!EmailValidator.getInstance().isValid(userDto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else {
            return userService.save(userDto);
        }
    }

    @PatchMapping("/{userId}")
    public UserDto put(@RequestBody UserDto userDto, @PathVariable long userId) throws UserNotFound {
        return userService.put(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) throws UserNotFound {
        userService.delete(userId);
    }

    @GetMapping
    public List<UserDto> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable long userId) throws UserNotFound {
        return userService.findById(userId);
    }
}
