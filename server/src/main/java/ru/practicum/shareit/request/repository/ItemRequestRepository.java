package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends PagingAndSortingRepository<ItemRequest, Long> {

    @Query(value = "select ir" +
            " from ItemRequest as ir" +
            " where ir.requester.id = :requesterId")
    List<ItemRequest> findByRequesterId(Long requesterId, Pageable pageable);

    @Query(value = "select ir" +
            " from ItemRequest as ir" +
            " where requester.id != :userId")
    List<ItemRequest> findAllFromOthers(Long userId, Pageable pageable);

}
