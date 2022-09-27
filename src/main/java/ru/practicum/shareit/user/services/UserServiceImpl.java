package ru.practicum.shareit.user.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.UserDtoBadRequest;
import ru.practicum.shareit.user.exceptions.UserDuplicatedEmail;
import ru.practicum.shareit.user.exceptions.UserNotFound;
import ru.practicum.shareit.user.exceptions.UserServerError;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositoryes.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

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
                return UserMapper.toUserDto(userStorage.save(user));
            } else {
                throw new UserDuplicatedEmail("Duplicated email %s", userDto.getEmail());
            }
        } else {
            throw new UserDtoBadRequest("Bad request for userDto", userDto);
        }
    }

    @Override
    public UserDto patch(UserDto userDto, long userId) throws UserNotFound {
        if (userDto != null) {
            if (userStorage.findAll().stream().noneMatch(user -> user.getEmail().equals(userDto.getEmail()))) {
                Optional<User> optionalUser = userStorage.findById(userId);
                if (optionalUser.isPresent()) {
                    User user = optionalUser.get();
                    user.setId(userId);
                    if (userDto.getName() != null) {
                        user.setName(userDto.getName());
                    }
                    //TODO Валидация email...
                    if (userDto.getEmail() != null && validate(userDto.getEmail())) {
                        user.setEmail(userDto.getEmail());
                    }
                    userStorage.save(user);
                    return UserMapper.toUserDto(user);
                } else {
                    throw new UserDuplicatedEmail("Duplicated email %s", userDto.getEmail());
                }
            } else {
                throw new UserServerError("User already exist");
            }
        } else {
            throw new UserNotFound("User %s not found", userId);
        }
    }

    @Override
    public void delete(long userId) throws UserNotFound {
        Optional<User> optionalUser = userStorage.findById(userId);
        if (optionalUser.isPresent()) {
            userStorage.delete(optionalUser.get());
        } else  {
            throw new UserNotFound("User %s not found", userId);
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
        Optional<User> optionalUser = userStorage.findById(userId);
        if (optionalUser.isPresent()) {
            return UserMapper.toUserDto(optionalUser.get());
        } else {
            throw new UserNotFound("User %s not found", userId);
        }
    }

    //TODO Валидация Email
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX_1 =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validate(String emailStr) {
        Matcher matcher1 = VALID_EMAIL_ADDRESS_REGEX_1.matcher(emailStr);
        return matcher1.find();
    }
}
