package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends PagingAndSortingRepository<Booking, Long> {

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

    // ALL COMPLETED BY_BOOKER
    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.booker as booker" +
            " JOIN b.item as i" +
            " WHERE booker.id = :bookerId" +
            " AND i.id = :itemId" +
            " AND b.end < :now" +
            " ORDER BY b.start DESC")
    List<Booking> findAllCompletedBookingsByBookerIdAndItemId(Long bookerId, Long itemId, LocalDateTime now);

    // ALL BY_BOOKER
    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.booker as booker" +
            " JOIN b.item as i" +
            " WHERE booker.id = :userId" +
            " ORDER BY b.start DESC")
    Page<Booking> findAllBookingsByUserId(Long userId, Pageable pageable);

    // CURRENT BY_BOOKER
    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.booker as booker" +
            " JOIN b.item as i" +
            " WHERE booker.id = :userId" +
            " AND (b.start < :now" +
            " AND b.end > :now)" +
            " ORDER BY b.start DESC")
    Page<Booking> findAllBookingsByUserIdCurrent(Long userId, LocalDateTime now, Pageable pageable);

    // PAST BY_BOOKER
    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.booker as booker" +
            " JOIN b.item as i" +
            " WHERE booker.id = :userId" +
            " AND b.end < :now" +
            " ORDER BY b.start DESC")
    Page<Booking> findAllBookingsByUserIdPast(Long userId, LocalDateTime now, Pageable pageable);

    // FUTURE BY_BOOKER
    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.booker as booker" +
            " JOIN b.item as i" +
            " WHERE booker.id = :userId" +
            " AND b.start > :now" +
            " ORDER BY b.start DESC")
    Page<Booking> findAllBookingsByUserIdFuture(Long userId, LocalDateTime now, Pageable pageable);

    // WAITING BY_BOOKER
    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.booker as booker" +
            " JOIN b.item as i" +
            " WHERE booker.id = :userId" +
            " AND b.status = 'WAITING'" +
            " ORDER BY b.start DESC")
    Page<Booking> findAllBookingsByUserIdWaiting(Long userId, Pageable pageable);

    // REJECTED BY_BOOKER
    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.booker as booker" +
            " JOIN b.item as i" +
            " WHERE booker.id = :userId" +
            " AND b.status = 'REJECTED'" +
            " ORDER BY b.start DESC")
    Page<Booking> findAllBookingsByUserIdRejected(Long userId, Pageable pageable);

    // OWNER STARTS !!!!!!!!!!!!!!!!!!!

    // ALL BY_OWNER
    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.item as i" +
            " JOIN i.owner as o" +
            " WHERE o.id = :userId" +
            " ORDER BY b.start DESC")
    Page<Booking> findAllBookingsByOwnerId(Long userId, Pageable pageable);

    // CURRENT BY_OWNER
    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.item as i" +
            " JOIN i.owner as o" +
            " WHERE o.id = :userId" +
            " AND (b.start < :now" +
            " AND b.end > :now)" +
            " ORDER BY b.start DESC")
    Page<Booking> findAllBookingsByOwnerIdCurrent(Long userId, LocalDateTime now, Pageable pageable);

    // PAST BY_OWNER
    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.item as i" +
            " JOIN i.owner as o" +
            " WHERE o.id = :userId" +
            " AND b.end < :now" +
            " ORDER BY b.start DESC")
    Page<Booking> findAllBookingsByOwnerIdPast(Long userId, LocalDateTime now, Pageable pageable);

    // FUTURE BY_OWNER
    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.item as i" +
            " JOIN i.owner as o" +
            " WHERE o.id = :userId" +
            " AND b.start > :now" +
            " ORDER BY b.start DESC")
    Page<Booking> findAllBookingsByOwnerIdFuture(Long userId, LocalDateTime now, Pageable pageable);

    // WAITING BY_OWNER
    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.item as i" +
            " JOIN i.owner as o" +
            " WHERE o.id = :userId" +
            " AND b.status = 'WAITING'" +
            " ORDER BY b.start DESC")
    Page<Booking> findAllBookingsByOwnerIdWaiting(Long userId, Pageable pageable);

    // REJECTED BY_OWNER
    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.item as i" +
            " JOIN i.owner as o" +
            " WHERE o.id = :userId" +
            " AND b.status = 'REJECTED'" +
            " ORDER BY b.start DESC")
    Page<Booking> findAllBookingsByOwnerIdRejected(Long userId, Pageable pageable);

}

   /* @Query(value = "select ir" +
            " from ItemRequest as ir" +
            " where requester.id != :requesterId" +
            " order by ir.created")
    Page<ItemRequest> findAllFromOthersWithParams(Long requesterId, Pageable pageable);*/
