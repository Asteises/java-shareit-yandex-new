package ru.practicum.shareit.user.exceptions;

public class UserNotOwner extends RuntimeException {
    public UserNotOwner(String message, long userId) {
        super(message);
    }
}
