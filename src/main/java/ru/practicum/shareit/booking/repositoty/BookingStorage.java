package ru.practicum.shareit.booking.repositoty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingStorage extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerOrderByStartDesc(User user);

    @Query(value = "select * from BOOKINGS B " +
            "where B.BOOKER_ID = ?1 " +
            "and (B.STATUS = ?2 or B.STATUS = ?3) " +
            "order by B.START_DATE desc",
            nativeQuery = true)
    List<Booking> findAllByBookerAndStatusFutureOrderByStartDesc(long bookerId, String status1, String status2);

    @Query(value = "select * from BOOKINGS B " +
            "join ITEMS I on I.ID = B.ITEM_ID " +
            "where B.BOOKER_ID = ?1 " +
            "and B.STATUS = ?2 " +
            "order by B.START_DATE desc",
            nativeQuery = true)
    List<Booking> findAllByBookerAndStatusOrderByStartDesc(long userId, String status);

    List<Booking> findAllByBooker_IdAndEndIsBefore(long bookerId, LocalDateTime end);

    List<Booking> findAllByBooker_idAndEndIsAfterAndStartIsBefore(long bookerId, LocalDateTime end, LocalDateTime start);

    @Query(value = "select * from BOOKINGS B " +
            "join ITEMS I on I.ID = B.ITEM_ID " +
            "where I.OWNER_ID = ?1 " +
            "and B.END_DATE < ?2 " +
            "order by B.START_DATE desc ",
            nativeQuery = true)
    List<Booking> findAllByOwner_IdAndEndIsBefore(long ownerId, LocalDateTime end);


    @Query(value = "select * from BOOKINGS B " +
            "join ITEMS I on I.ID = B.ITEM_ID " +
            "where I.OWNER_ID = ?1 " +
            "and (B.END_DATE > ?2 and B.START_DATE < ?3)" +
            "order by B.START_DATE desc ",
            nativeQuery = true)
    List<Booking> findAllByOwner_IdAndEndIsAfterAndStartIsBefore(long ownerId, LocalDateTime end, LocalDateTime start);

    @Query(value = "select * from BOOKINGS B " +
            "join ITEMS I on I.ID = B.ITEM_ID " +
            "where I.OWNER_ID = ?1 " +
            "order by B.START_DATE desc ",
            nativeQuery = true)
    List<Booking> findAllByItemOwner(long userId);

    @Query(value = "select * from BOOKINGS B " +
            "join ITEMS I on I.ID = B.ITEM_ID " +
            "where I.OWNER_ID = ?1 " +
            "and B.STATUS = ?2 " +
            "order by B.START_DATE desc",
            nativeQuery = true)
    List<Booking> findAllByItemOwnerAndStatusOrderByStartDesc(long userId, String status);

    @Query(value = "select * from BOOKINGS B " +
            "join ITEMS I on I.ID = B.ITEM_ID " +
            "where I.OWNER_ID = ?1 " +
            "and (B.STATUS = ?2 or B.STATUS = ?3)" +
            "order by B.START_DATE desc ",
            nativeQuery = true)
    List<Booking> findAllByOwnerAndStatusFutureOrderByStartDesc(long ownerId, String status1, String status2);

    Optional<Booking> findByItemAndBooker(Item item, User bookerId);

    List<Booking> findByItem_IdAndBooker_IdOrderByStartDesc(long itemId, long bookerId);

    Booking findFirstByItem_idAndEndBeforeOrderByEndDesc(long itemId, LocalDateTime end);

    Booking findFirstByItem_idAndStartAfterOrderByStartDesc(long itemId, LocalDateTime start);

}
