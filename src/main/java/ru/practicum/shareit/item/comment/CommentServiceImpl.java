package ru.practicum.shareit.item.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.exceptions.ItemNotFound;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.services.ItemService;
import ru.practicum.shareit.user.exceptions.UserNotBooker;
import ru.practicum.shareit.user.exceptions.UserNotFound;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.services.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentStorage commentStorage;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;

    @Override
    public CommentDto postComment(long itemId, long userId, String text)
            throws ItemNotFound, UserNotFound, UserNotBooker {
        User user = userService.checkUser(userId);
        Item item = itemService.checkItem(itemId);
        List<Booking> bookings = bookingService.findAllBookingByItemIdAndBooker(itemId, userId);
        if (!bookings.isEmpty()) {
            Comment comment = new Comment();
            comment.setAuthor(user);
            comment.setItem(item);
            comment.setText(text);
            comment = commentStorage.save(comment);
            return CommentMapper.toCommentDto(comment);
        } else {
            throw new UserNotBooker("This User not Booker for this Item", userId);
        }
    }
}
