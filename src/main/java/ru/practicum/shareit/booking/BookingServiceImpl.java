package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.exceptions.ItemNotFound;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositores.ItemStorage;
import ru.practicum.shareit.user.exceptions.UserNotFound;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositoryes.UserStorage;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Override
    public BookingDto save(BookingDto bookingDto, long userId) throws UserNotFound, ItemNotFound {
        Optional<User> optionalUser = userStorage.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Optional<Item> optionalItem = itemStorage.findById(bookingDto.getItemId());
            if (optionalItem.isPresent()) {
                Item item = optionalItem.get();
                Booking booking = BookingMapper.toBooking(bookingDto, item, user);
                bookingRepository.save(booking);
                return BookingMapper.toBookingDto(booking);
            } else {
                throw new ItemNotFound("Item not found", bookingDto.getItemId());
            }
        } else {
            throw new UserNotFound("User not found", userId);
        }
    }
}
