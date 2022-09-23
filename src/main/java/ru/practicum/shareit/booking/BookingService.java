package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.exceptions.ItemNotFound;
import ru.practicum.shareit.user.exceptions.UserNotFound;

@Service
public interface BookingService {

    BookingDto save(BookingDto bookingDto, long userId) throws UserNotFound, ItemNotFound;

}
