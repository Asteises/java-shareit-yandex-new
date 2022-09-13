package ru.practicum.shareit.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.item.exceptions.ItemNotFound;
import ru.practicum.shareit.item.exceptions.ItemNullParametr;
import ru.practicum.shareit.user.exceptions.UserDtoBadRequest;
import ru.practicum.shareit.user.exceptions.UserDuplicatedEmail;
import ru.practicum.shareit.user.exceptions.UserNotFound;
import ru.practicum.shareit.user.exceptions.UserServerError;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({UserNotFound.class, ItemNotFound.class})
    public ResponseEntity<String> userNotFoundHandler(final RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Something not found");
    }

    @ExceptionHandler({UserDuplicatedEmail.class})
    public ResponseEntity<String> userDuplicatedEmail(final RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Duplicated email");
    }

    @ExceptionHandler({ItemNullParametr.class})
    public ResponseEntity<String> itemBadParam(final RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad or null parameter for Item");
    }

    @ExceptionHandler({UserDtoBadRequest.class})
    public ResponseEntity<String> userBadRequest(final RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request or null for UserDto");
    }

    @ExceptionHandler({UserServerError.class})
    public ResponseEntity<String> userServerError(final RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Bad request or null for User");
    }
}
