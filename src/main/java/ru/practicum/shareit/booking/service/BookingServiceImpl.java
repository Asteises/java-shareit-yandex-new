package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.exception.BookingNotFound;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repositoty.BookingStorage;
import ru.practicum.shareit.item.exceptions.ItemNotFound;
import ru.practicum.shareit.item.exceptions.ItemNullParametr;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositores.ItemStorage;
import ru.practicum.shareit.user.exceptions.UserNotBooker;
import ru.practicum.shareit.user.exceptions.UserNotFound;
import ru.practicum.shareit.user.exceptions.UserNotOwner;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositoryes.UserStorage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingStorage bookingRepository;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Override
    public BookingDto save(BookingDto bookingDto, long userId) throws UserNotFound, ItemNotFound, TimeoutException {
        if (validateDates(bookingDto.getStart(), bookingDto.getEnd())) {
            Optional<User> optionalUser = userStorage.findById(userId);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                Optional<Item> optionalItem = itemStorage.findById(bookingDto.getItemId());
                if (optionalItem.isPresent()) {
                    Item item = optionalItem.get();
                    if (item.getAvailable()) {
                        Booking booking = BookingMapper.toBooking(bookingDto, item, user, BookingStatus.WAITING);
                        bookingRepository.save(booking);
                        return BookingMapper.toBookingDto(booking);
                    } else {
                        throw new ItemNullParametr("Item is FALSE");
                    }
                } else {
                    throw new ItemNotFound("Item not found", bookingDto.getItemId());
                }
            } else {
                throw new UserNotFound("User not found", userId);
            }
        } else {
            throw new TimeoutException("Wrong Time");
        }

    }

    @Override
    public BookingDto ownerDecision(long bookingId,
                                    long userId,
                                    Boolean isApproved) throws BookingNotFound, UserNotOwner {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            if (booking.getItem().getOwner().getId().equals(userId)) {
                if (isApproved) {
                    booking.setStatus(BookingStatus.APPROVED);
                } else {
                    booking.setStatus(BookingStatus.REJECTED);
                }
                bookingRepository.save(booking);
                return BookingMapper.toBookingDto(booking);
            } else {
                throw new UserNotOwner("This User not Owner for this Item", userId);
            }
        } else {
            throw new BookingNotFound("Booking not found", bookingId);
        }
    }

    @Override
    public BookingDto getBooking(long bookingId, long userId)
            throws BookingNotFound, UserNotFound, UserNotOwner, UserNotBooker {

        //TODO Попробовать сделать все это через классы валиадации
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);

        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            Optional<User> optionalUser = userStorage.findById(userId);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                if (booking.getBooker().equals(user)) {
                    if (booking.getItem().getOwner().equals(user)) {
                        return BookingMapper.toBookingDto(booking);
                    } else {
                        throw new UserNotOwner("User not Owner", userId);
                    }
                } else {
                    throw new UserNotBooker("User not Booker", userId);
                }
            } else {
                throw new UserNotFound("User not found", userId);
            }
        } else {
            throw new BookingNotFound("Booking not found", bookingId);
        }
    }

    @Override
    public List<BookingDto> getAllBookingsByBooker(String state, long userId) throws UserNotFound {
        Optional<User> optionalUser = userStorage.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            //TODO Сделать проверку Booker
            if (state.equals("ALL")) {
                return bookingRepository.findAllByBooker(user).stream()
                        .sorted(Comparator.comparing(Booking::getStart))
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            return bookingRepository.findAllByBookerAndStatus(user, BookingStatus.fromString(state)).stream()
                    .sorted(Comparator.comparing(Booking::getStart))
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else {
            throw new UserNotFound("User not found", userId);
        }
    }

    @Override
    public List<BookingDto> getAllBookingsByOwner(String state, long userId) throws UserNotFound {
        Optional<User> optionalUser = userStorage.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
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
        } else {
            throw new UserNotFound("User not found", userId);
        }
        return new ArrayList<>();
    }

    public static boolean validateDates(LocalDateTime start, LocalDateTime end) {
        try {
            LocalDateTime current = LocalDateTime.now();
            return (start.isEqual(current) || start.isAfter(current)) && end.isAfter(start);
        } catch (DateTimeParseException ex) {
            ex.printStackTrace();
        }
        return false;
    }

}
