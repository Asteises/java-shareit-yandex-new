package ru.practicum.shareit.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.item.exceptions.ItemNotFound;
import ru.practicum.shareit.user.exceptions.UserNotFound;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({UserNotFound.class, ItemNotFound.class})
    public ResponseEntity<String> runtimeHandler(final RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Что-то пошло не так, но мы не знаем что. Пардоньте...");
    }
}
