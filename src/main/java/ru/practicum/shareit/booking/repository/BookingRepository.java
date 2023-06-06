package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.id = :itemId and " +
            "b.status = 'APPROVED' ")
    List<Booking> findByItemId(Long itemId);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.id in(:itemIdList) and " +
            "b.status = 'APPROVED' ")
    List<Booking> findByItemIdList(List<Long> itemIdList);

    // ALL BY_BOOKER
    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.booker as booker" +
            " JOIN b.item as i" +
            " WHERE booker.id = ?1" +
            " AND i.id = ?2" +
            " AND b.end < ?3" +
            " ORDER BY b.start DESC")
    List<Booking> findAllCompletedBookingsByBookerIdAndItemId(Long bookerId, Long itemId, LocalDateTime now);

    // ALL BY_BOOKER
    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.booker as booker" +
            " JOIN b.item as i" +
            " WHERE booker.id = ?1" +
            " ORDER BY b.start DESC")
    List<Booking> findAllBookingsByUserId(Long userId);

    // CURRENT BY_BOOKER
    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.booker as booker" +
            " JOIN b.item as i" +
            " WHERE booker.id = ?1" +
            " AND (b.start < ?2" +
            " AND b.end > ?2)" +
            " ORDER BY b.start DESC")
    List<Booking> findAllBookingsByUserIdCurrent(Long userId, LocalDateTime now);

    // PAST BY_BOOKER
    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.booker as booker" +
            " JOIN b.item as i" +
            " WHERE booker.id = ?1" +
            " AND b.end < ?2" +
            " ORDER BY b.start DESC")
    List<Booking> findAllBookingsByUserIdPast(Long userId, LocalDateTime now);

    // FUTURE BY_BOOKER
    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.booker as booker" +
            " JOIN b.item as i" +
            " WHERE booker.id = ?1" +
            " AND b.start > ?2" +
            " ORDER BY b.start DESC")
    List<Booking> findAllBookingsByUserIdFuture(Long userId, LocalDateTime now);

    // WAITING BY_BOOKER
    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.booker as booker" +
            " JOIN b.item as i" +
            " WHERE booker.id = ?1" +
            " AND b.status = 'WAITING'" +
            " ORDER BY b.start DESC")
    List<Booking> findAllBookingsByUserIdWaiting(Long userId);

    // REJECTED BY_BOOKER
    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.booker as booker" +
            " JOIN b.item as i" +
            " WHERE booker.id = ?1" +
            " AND b.status = 'REJECTED'" +
            " ORDER BY b.start DESC")
    List<Booking> findAllBookingsByUserIdRejected(Long userId);

    // OWNER STARTS !!!!!!!!!!!!!!!!!!!

    // ALL BY_OWNER
    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.item as i" +
            " JOIN i.owner as o" +
            " WHERE o.id = ?1" +
            " ORDER BY b.start DESC")
    List<Booking> findAllBookingsByOwnerId(Long userId);

    // CURRENT BY_OWNER
    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.item as i" +
            " JOIN i.owner as o" +
            " WHERE o.id = ?1" +
            " AND (b.start < ?2" +
            " AND b.end > ?2)" +
            " ORDER BY b.start DESC")
    List<Booking> findAllBookingsByOwnerIdCurrent(Long userId, LocalDateTime now);

    // PAST BY_OWNER
    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.item as i" +
            " JOIN i.owner as o" +
            " WHERE o.id = ?1" +
            " AND b.end < ?2" +
            " ORDER BY b.start DESC")
    List<Booking> findAllBookingsByOwnerIdPast(Long userId, LocalDateTime now);

    // FUTURE BY_OWNER
    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.item as i" +
            " JOIN i.owner as o" +
            " WHERE o.id = ?1" +
            " AND b.start > ?2" +
            " ORDER BY b.start DESC")
    List<Booking> findAllBookingsByOwnerIdFuture(Long userId, LocalDateTime now);

    // WAITING BY_OWNER
    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.item as i" +
            " JOIN i.owner as o" +
            " WHERE o.id = ?1" +
            " AND b.status = 'WAITING'" +
            " ORDER BY b.start DESC")
    List<Booking> findAllBookingsByOwnerIdWaiting(Long userId);

    // REJECTED BY_OWNER
    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.item as i" +
            " JOIN i.owner as o" +
            " WHERE o.id = ?1" +
            " AND b.status = 'REJECTED'" +
            " ORDER BY b.start DESC")
    List<Booking> findAllBookingsByOwnerIdRejected(Long userId);

}
