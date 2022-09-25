package ru.practicum.shareit.item.comment;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.exceptions.ItemNotFound;
import ru.practicum.shareit.user.exceptions.UserNotBooker;
import ru.practicum.shareit.user.exceptions.UserNotFound;

@Service
public interface CommentService {

    CommentDto postComment(long itemId, long userId, String text)
            throws UserNotFound, ItemNotFound, UserNotBooker;
}
