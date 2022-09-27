package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.BookingNotFound;
import ru.practicum.shareit.booking.exception.BookingWrongTime;
import ru.practicum.shareit.item.exceptions.ItemNotFound;
import ru.practicum.shareit.user.exceptions.UserNotBooker;
import ru.practicum.shareit.user.exceptions.UserNotFound;
import ru.practicum.shareit.user.exceptions.UserNotOwner;

import java.util.List;
import java.util.concurrent.TimeoutException;

@Service
public interface BookingService {

    BookingDto save(BookingDto bookingDto, long userId)
            throws UserNotFound, ItemNotFound, BookingWrongTime;

    BookingDto ownerDecision(long bookingId, long userId, boolean approved)
            throws BookingNotFound, UserNotOwner;

    BookingDto getBooking(long bookingId, long userID)
            throws BookingNotFound, UserNotFound, UserNotOwner, UserNotBooker;

    List<BookingDto> getAllBookingsByBooker(String state, long userId) throws UserNotFound;

    List<BookingDto> getAllBookingsByOwner(String state, long userId) throws UserNotFound;
}
