package ru.practicum.shareit.user.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.UserNotFound;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositoryes.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    private long userID = 0;

    @Override
    public UserDto save(UserDto userDto) {
        if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {
            User checkUser =
                    userStorage.findAll().stream()
                            .filter(user -> user.getEmail().equals(userDto.getEmail()))
                            .findFirst()
                            .orElse(null);
            if (checkUser == null) {
                User user = UserMapper.toUser(userDto);
                user.setId(++userID);
                return UserMapper.toUserDto(userStorage.save(user));
            } else {
                throw new RuntimeException("Duplicated email");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public UserDto put(UserDto userDto, long userId) throws UserNotFound {
        if (userDto != null && userStorage.findById(userId) != null) {
            User checkUser =
                    userStorage.findAll().stream()
                            .filter(user -> user.getEmail().equals(userDto.getEmail()))
                            .findFirst()
                            .orElse(null);
            if (checkUser == null) {
                User user = userStorage.findById(userId);
                user.setId(userId);
                if (userDto.getName() != null) {
                    user.setName(userDto.getName());
                }
                if (userDto.getEmail() != null) {

                    user.setEmail(userDto.getEmail());
                }
                userStorage.put(user, userId);
                return UserMapper.toUserDto(user);
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            throw new UserNotFound(String.format("User %s not found", userId));
        }
    }

    @Override
    public void delete(long userId) throws UserNotFound {
        if (userStorage.findById(userId) != null) {
            userStorage.delete(userId);
        } else {
            throw new UserNotFound(String.format("User %s not found", userId));
        }
    }

    @Override
    public List<UserDto> findAll() {
        return userStorage.findAll().stream()
                .map(user -> new UserDto(user.getId(), user.getName(), user.getEmail()))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findById(long userId) throws UserNotFound {
        try {
            return UserMapper.toUserDto(userStorage.findById(userId));
        } catch (UserNotFound e) {
            throw new UserNotFound(String.format("User %s not found", userId));
        }
    }
}
