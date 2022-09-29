package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.exception.BookingNotFound;
import ru.practicum.shareit.booking.exception.BookingWrongTime;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repositoty.BookingStorage;
import ru.practicum.shareit.item.exceptions.ItemNullParametr;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.services.ItemService;
import ru.practicum.shareit.user.exceptions.UserNotFound;
import ru.practicum.shareit.user.exceptions.UserNotOwner;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.services.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
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
    public BookingDto createBooking(BookingDto bookingDto, long userId) throws BookingWrongTime, BookingNotFound {
        if (validateDates(bookingDto.getStart(), bookingDto.getEnd())) {
            User booker = userService.checkUser(userId);
            Item currentItem = itemService.checkItem(bookingDto.getItemId());
            if (currentItem.getOwner().equals(booker)) {
                throw new BookingNotFound("Booker can't book own Item");
            }
            if (currentItem.getAvailable()) {
                Booking booking = BookingMapper.toBooking(bookingDto);
                booking.setItem(currentItem);
                booking.setBooker(booker);
                Optional<Booking> optionalBooking = bookingRepository.findByItemAndBooker(currentItem, booker);
                if (optionalBooking.isPresent() && optionalBooking.get().getStatus().equals(BookingStatus.WAITING)) {
                    booking.setId(optionalBooking.get().getId());
                }
                if (optionalBooking.isPresent() && optionalBooking.get().getStatus().equals(BookingStatus.REJECTED)) {
                    throw new BookingNotFound("Booking REJECTED");
                }
                booking.setStatus(BookingStatus.WAITING);
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
    public BookingResponseDto ownerDecision(long bookingId, long ownerId, boolean approved)
            throws UserNotFound, ItemNullParametr {
        Booking booking = checkBooking(bookingId);
        if (booking.getItem().getOwner().getId().equals(ownerId)) {
            if (booking.getStatus().equals(BookingStatus.WAITING)) {
                if (approved) {
                    booking.setStatus(BookingStatus.APPROVED);
                } else {
                    booking.setStatus(BookingStatus.REJECTED);
                }
                bookingRepository.save(booking);
                return BookingMapper.toBookingResponseDto(booking);
            } else {
                throw new ItemNullParametr("Status not WAITING");
            }
        } else {
            throw new UserNotFound("This User not Owner for this Item");
        }
    }

    @Override
    public BookingResponseDto getBooking(long bookingId, long userId) throws UserNotFound {
        Booking booking = checkBooking(bookingId);
        User user = userService.checkUser(userId);
        if (booking.getBooker().equals(user) || booking.getItem().getOwner().equals(user)) {
            return BookingMapper.toBookingResponseDto(booking);
        } else {
            throw new UserNotFound("User not Booker and not Owner");
        }
    }

    @Override
    public List<BookingResponseDto> getAllBookingsByBooker(String state, long userId) throws ItemNullParametr {
        User booker = userService.checkUser(userId);
        if (state.equals("ALL")) {
            return bookingRepository.findAllByBookerOrderByStartDesc(booker).stream()
                    .map(BookingMapper::toBookingResponseDto)
                    .collect(Collectors.toList());
        }
        if (state.equals("FUTURE")) {
            return bookingRepository
                    .findAllByBookerAndStatusFutureOrderByStartDesc(userId,
                            BookingStatus.WAITING.name(),
                            BookingStatus.APPROVED.name()).stream()
                    .map(BookingMapper::toBookingResponseDto)
                    .collect(Collectors.toList());
        }
        throw new ItemNullParametr("Unknown state: UNSUPPORTED_STATUS");
    }

    @Override
    public List<BookingResponseDto> getAllBookingsByOwner(String state, long userId) throws UserNotFound {
        userService.checkUser(userId);
        if (state.equals("ALL")) {
            return bookingRepository.findAllByItemOwner(userId).stream()
                    .map(BookingMapper::toBookingResponseDto)
                    .collect(Collectors.toList());
        }
        if (state.equals("FUTURE")) {
            return bookingRepository.findAllByOwnerAndStatusFutureOrderByStartDesc(userId,
                            BookingStatus.APPROVED.name(),
                            BookingStatus.WAITING.name()).stream()
                    .map(BookingMapper::toBookingResponseDto)
                    .collect(Collectors.toList());
        }
        if (state.equals("CURRENT")) {
            return bookingRepository.findAllByItemOwnerAndStatus(userId, BookingStatus.APPROVED.name()).stream()
                    .map(BookingMapper::toBookingResponseDto)
                    .collect(Collectors.toList());
        }
        if (state.equals("PAST")) {
            return bookingRepository.findAllByItemOwnerAndStatus(userId, BookingStatus.CANCELED.name()).stream()
                    .map(BookingMapper::toBookingResponseDto)
                    .collect(Collectors.toList());
        }
        if (state.equals("WAITING")) {
            return bookingRepository.findAllByItemOwnerAndStatus(userId, BookingStatus.WAITING.name()).stream()
                    .map(BookingMapper::toBookingResponseDto)
                    .collect(Collectors.toList());
        }
        if (state.equals("REJECTED")) {
            return bookingRepository.findAllByItemOwnerAndStatus(userId, BookingStatus.REJECTED.name()).stream()
                    .map(BookingMapper::toBookingResponseDto)
                    .collect(Collectors.toList());
        }
        throw new ItemNullParametr("Unknown state: UNSUPPORTED_STATUS");
    }

    @Override
    public Booking getLastBookingByItem(long userId, long itemId) {
        User owner = userService.checkUser(userId);
        Item item = itemService.checkItem(itemId);
        if (item.getOwner().equals(owner)) {
            return bookingRepository.findFirstByItem_idAndEndBeforeOrderByEndDesc(itemId, LocalDateTime.now());
        }
        throw new UserNotOwner("User not Owner");
    }

    @Override
    public Booking getNextBookingByItem(long userId, long itemId) {
        User owner = userService.checkUser(userId);
        Item item = itemService.checkItem(itemId);
        if (item.getOwner().equals(owner)) {
            return bookingRepository.findFirstByItem_idAndStartAfterOrderByStartDesc(itemId, LocalDateTime.now());
        }
        throw new UserNotOwner("User not Owner");
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
