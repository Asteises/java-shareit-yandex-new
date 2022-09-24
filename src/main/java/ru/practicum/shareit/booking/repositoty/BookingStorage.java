package ru.practicum.shareit.booking.repositoty;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingStorage extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBooker(User user);
    List<Booking> findAllByBookerAndStatus(User user, BookingStatus status);
    @Query(value = "select * from BOOKINGS B " +
            "join USERS_ITEMS UI on UI.ITEM_ID = B.ITEM_ID " +
            "where UI.USER_ID = :userId",
            nativeQuery = true)
    List<Booking> findAllByItemOwner(long userId);

    @Query(value = "select * from BOOKINGS B " +
            "join USERS_ITEMS UI on UI.ITEM_ID = B.ITEM_ID " +
            "where UI.USER_ID = :userId and B.STATUS = :status",
            nativeQuery = true)
    List<Booking> findAllByItemOwnerAndStatus(long userId, BookingStatus status);

    Booking findByItemAndBooker(Item item, User bookerId);

    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);
}
