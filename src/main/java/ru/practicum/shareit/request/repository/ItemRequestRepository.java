package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutput;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends PagingAndSortingRepository<ItemRequest, Long> {

    @Query(value = "select ir" +
            " from ItemRequest as ir" +
            " where ir.requester.id = :requesterId" +
            " order by ir.created")
    List<ItemRequest> findByRequesterId(Long requesterId);

    @Query(value = "select ir" +
            " from ItemRequest as ir" +
            " where requester.id != :requesterId" +
            " order by ir.created")
    Page<ItemRequest> findAllFromOthers(Long requesterId, Pageable pageable);

}
