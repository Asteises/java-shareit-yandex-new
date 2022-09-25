package ru.practicum.shareit.item.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.ItemNotFound;
import ru.practicum.shareit.item.services.ItemService;
import ru.practicum.shareit.user.exceptions.UserNotFound;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto save(@RequestBody ItemDto itemDto,
                        @RequestHeader("X-Sharer-User-Id") long userId) throws UserNotFound {
        return itemService.save(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto patch(@RequestBody ItemDto itemDto,
                         @PathVariable long itemId,
                         @RequestHeader("X-Sharer-User-Id") long userId) throws ItemNotFound, UserNotFound {
        return itemService.put(itemDto, itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable long itemId) {
        itemService.delete(itemId);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto findById(@PathVariable long itemId) {
        return itemService.findById(itemId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> findAllByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.findAllByUserId(userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> findAllByItemName(@RequestParam String text) {
        return itemService.findAllByItemName(text);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto postComment(@PathVariable long itemId,
                                  @RequestHeader("X-Sharer-User-Id") long userId,
                                  @RequestParam String text) {
        return commentService.postComment(itemId, userId, text);
    }
}
