package ru.practicum.shareit.item.comment;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.exceptions.UserNotFound;

@Service
public interface CommentService {

    Comment postComment(long itemId, long userId, String text) throws UserNotFound;
}
