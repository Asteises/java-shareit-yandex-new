package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.BookingNotFound;
import ru.practicum.shareit.item.exceptions.ItemNotFound;
import ru.practicum.shareit.user.exceptions.UserNotBooker;
import ru.practicum.shareit.user.exceptions.UserNotFound;
import ru.practicum.shareit.user.exceptions.UserNotOwner;

import java.util.List;

@Service
public interface BookingService {

    BookingDto save(BookingDto bookingDto, long userId)
            throws UserNotFound, ItemNotFound;

    BookingDto ownerDecision(long bookingId, long userId, Boolean isApproved)
            throws BookingNotFound, UserNotOwner;

    BookingDto getBooking(long bookingId, long userID)
            throws BookingNotFound, UserNotFound, UserNotOwner, UserNotBooker;

    List<BookingDto> getAllBookingsByBooker(String state, long userId) throws UserNotFound;
}
