package ru.practicum.shareit.exception;

//400
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
