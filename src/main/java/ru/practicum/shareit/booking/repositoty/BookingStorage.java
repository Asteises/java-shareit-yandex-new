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
            "join ITEMS I on I.ID = B.ITEM_ID " +
            "where I.OWNER_ID = ?1",
            nativeQuery = true)
    List<Booking> findAllByItemOwner(long userId);

    @Query(value = "select * from BOOKINGS B " +
            "join ITEMS I on I.ID = B.ITEM_ID " +
            "where I.OWNER_ID = ?1 and B.STATUS = ?2",
            nativeQuery = true)
    List<Booking> findAllByItemOwnerAndStatus(long userId, BookingStatus status);

    Booking findByItemAndBooker(Item item, User bookerId);

    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);
}
