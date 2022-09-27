package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.BookingNotFound;
import ru.practicum.shareit.user.exceptions.UserNotBooker;
import ru.practicum.shareit.user.exceptions.UserNotFound;
import ru.practicum.shareit.user.exceptions.UserNotOwner;

import java.util.List;
import java.util.concurrent.TimeoutException;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto save(@RequestBody BookingDto bookingDto,
                           @RequestHeader("X-Sharer-User-Id") long userId) throws TimeoutException {
        return bookingService.save(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto ownerDecision(@PathVariable long bookingId,
                                    @RequestHeader("X-Sharer-User-Id") long userId,
                                    @RequestParam Boolean isApproved) {
        return bookingService.ownerDecision(bookingId, userId, isApproved);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto getBooking(@PathVariable long bookingId,
                                 @RequestHeader("X-Sharer-User-Id") long userId)
            throws BookingNotFound, UserNotFound, UserNotOwner, UserNotBooker {
        return bookingService.getBooking(bookingId, userId);
    }

    /**
     * Параметр state необязательный и по умолчанию равен ALL (англ. «все»).
     * Также он может принимать значения CURRENT (англ. «текущие»),
     * **PAST** (англ. «завершённые»),
     * FUTURE (англ. «будущие»),
     * WAITING (англ. «ожидающие подтверждения»),
     * REJECTED (англ. «отклонённые»).
     * Бронирования должны возвращаться отсортированными по дате от более новых к более старым.
     */
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getAllBookingsByBooker(@RequestParam(defaultValue = "ALL") String state,
                                                  @RequestHeader("X-Sharer-User-Id") long userId) throws UserNotFound {
        return bookingService.getAllBookingsByBooker(state, userId);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getAllBookingsByOwner(@RequestParam(defaultValue = "ALL") String state,
                                                  @RequestHeader("X-Sharer-User-Id") long userId) throws UserNotFound {
        return bookingService.getAllBookingsByOwner(state, userId);
    }
}
