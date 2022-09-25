package ru.practicum.shareit.item.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repositoty.BookingStorage;
import ru.practicum.shareit.item.exceptions.ItemNotFound;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositores.ItemStorage;
import ru.practicum.shareit.user.exceptions.UserNotBooker;
import ru.practicum.shareit.user.exceptions.UserNotFound;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositoryes.UserStorage;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentStorage commentStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;
    private final BookingStorage bookingStorage;

    @Override
    public CommentDto postComment(long itemId, long userId, String text)
            throws ItemNotFound, UserNotFound, UserNotBooker {
        Optional<User> optionalUser = userStorage.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Optional<Item> optionalItem = itemStorage.findById(itemId);
            if (optionalItem.isPresent()) {
                Item item = optionalItem.get();
                Booking booking = bookingStorage.findByItemAndBooker(item, user);
                if (booking != null) {
                    Comment comment = new Comment();
                    comment.setAuthor(user);
                    comment.setItem(item);
                    comment.setText(text);
                    commentStorage.save(comment);
                    return CommentMapper.toCommentDto(comment);
                } else {
                    throw new UserNotBooker("This User not Booker for this Item", userId);
                }
            } else {
                throw new ItemNotFound("Item not found", itemId);
            }
        } else {
            throw new UserNotFound("User not found", userId);
        }
    }
}
