package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwnerId(Long ownerId);

    @Query("select i" +
            " from Item as i" +
            " join i.request as r" +
            " where r.id in :requestIds")
    List<Item> findByRequestIdList(List<Long> requestIds);

    @Query(value = "SELECT i " +
            "FROM Item AS i " +
            "WHERE (lower(i.name) LIKE %:text% OR lower(i.description) LIKE %:text%) AND i.available=TRUE")
    List<Item> findItemsByTextIgnoreCase(String text);

}
