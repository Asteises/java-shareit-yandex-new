package ru.practicum.shareit.user.exceptions;

import ru.practicum.shareit.user.dto.UserDto;

public class UserDtoBadRequest extends RuntimeException {

    public UserDtoBadRequest(String message, UserDto userDto) {
        super(message);
    }
}
