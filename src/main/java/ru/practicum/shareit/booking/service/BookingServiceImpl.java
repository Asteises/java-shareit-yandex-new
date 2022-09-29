package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.exception.BookingNotFound;
import ru.practicum.shareit.booking.exception.BookingWrongTime;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repositoty.BookingStorage;
import ru.practicum.shareit.item.exceptions.ItemNullParametr;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.services.ItemService;
import ru.practicum.shareit.user.exceptions.UserNotBooker;
import ru.practicum.shareit.user.exceptions.UserNotFound;
import ru.practicum.shareit.user.exceptions.UserNotOwner;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.services.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingStorage bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public BookingDto createBooking(BookingDto bookingDto, long userId) throws BookingWrongTime {
        if (validateDates(bookingDto.getStart(), bookingDto.getEnd())) {
            User booker = userService.checkUser(userId);
            Item currentItem = itemService.checkItem(bookingDto.getItemId());
            if (currentItem.getAvailable()) {
                Booking booking = BookingMapper.toBooking(bookingDto);
                booking.setItem(currentItem);
                booking.setBooker(booker);
                Optional<Booking> optionalBooking = bookingRepository.findByItemAndBooker(currentItem, booker);
                optionalBooking.ifPresent(value -> booking.setId(value.getId()));
                bookingRepository.save(booking);
                return BookingMapper.toBookingDto(booking);
            } else {
                throw new ItemNullParametr("Item is unavailable");
            }
        } else {
            throw new BookingWrongTime("Booking wrong Time");
        }

    }

    @Override
    public BookingDto ownerDecision(long bookingId, long ownerId, boolean approved) throws UserNotOwner {
        Booking booking = checkBooking(bookingId);
        if (booking.getItem().getOwner().getId().equals(ownerId)) {
            if (booking.getStatus().equals(BookingStatus.WAITING)) {
                if (approved) {
                    booking.setStatus(BookingStatus.APPROVED);
                } else {
                    booking.setStatus(BookingStatus.REJECTED);
                }
                bookingRepository.save(booking);
                return BookingMapper.toBookingDto(booking);
            } else {
                throw new ItemNullParametr("Status not WAITING");
            }
        } else {
            throw new UserNotOwner("This User not Owner for this Item");
        }
    }

    @Override
    public BookingDto getBooking(long bookingId, long userId) throws UserNotOwner, UserNotBooker {
        Booking booking = checkBooking(bookingId);
        User user = userService.checkUser(userId);
        if (booking.getBooker().equals(user)) {
            if (booking.getItem().getOwner().equals(user)) {
                return BookingMapper.toBookingDto(booking);
            } else {
                throw new UserNotOwner("User not Owner");
            }
        } else {
            throw new UserNotBooker("User not Booker", userId);
        }
    }

    @Override
    public List<BookingDto> getAllBookingsByBooker(String state, long userId) throws UserNotFound {
        User booker = userService.checkUser(userId);
            if (state.equals("ALL")) {
                return bookingRepository.findAllByBookerOrderByStartDesc(booker).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            return bookingRepository.findAllByBookerAndStatus(booker, BookingStatus.fromString(state)).stream()
                    .sorted(Comparator.comparing(Booking::getStart))
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllBookingsByOwner(String state, long userId) throws UserNotFound {
        User owner = userService.checkUser(userId);
            if (state.equals("ALL")) {
                return bookingRepository.findAllByItemOwner(userId).stream()
                        .sorted(Comparator.comparing(Booking::getStart))
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            if (state.equals("CURRENT")) {
                return bookingRepository.findAllByItemOwnerAndStatus(userId, BookingStatus.APPROVED.name()).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            if (state.equals("PAST")) {
                return bookingRepository.findAllByItemOwnerAndStatus(userId, BookingStatus.CANCELED.name()).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            if (state.equals("WAITING")) {
                return bookingRepository.findAllByItemOwnerAndStatus(userId, BookingStatus.WAITING.name()).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            if (state.equals("REJECTED")) {
                return bookingRepository.findAllByItemOwnerAndStatus(userId, BookingStatus.REJECTED.name()).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
        return new ArrayList<>();
    }

    @Override
    public Booking checkBooking(long bookingId) throws BookingNotFound {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isPresent()) {
            return optionalBooking.get();
        } else {
            throw new BookingNotFound(String.format("Booking by ID: %s - not found", bookingId));
        }
    }

    public static boolean validateDates(LocalDateTime start, LocalDateTime end) {
        try {
            LocalDateTime current = LocalDateTime.now();
            return (start.isEqual(current) || start.isAfter(current)) && end.isAfter(start) && end.isAfter(current);
        } catch (DateTimeParseException ex) {
            ex.printStackTrace();
        }
        return false;
    }

}
