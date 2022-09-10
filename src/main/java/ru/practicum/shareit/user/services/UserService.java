package ru.practicum.shareit.user.services;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.UserNotFound;

import java.util.List;

@Service
public interface UserService {

    UserDto save(UserDto userDto);

    UserDto put(UserDto userDto, long userId) throws UserNotFound;

    void delete(long userId) throws UserNotFound;

    List<UserDto> findAll();

    UserDto findById(long userId) throws UserNotFound;
}
