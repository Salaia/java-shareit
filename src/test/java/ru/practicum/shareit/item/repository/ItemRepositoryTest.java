package ru.practicum.shareit.item.repository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRepositoryTest {
    @Autowired
    TestEntityManager entityManager;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;

    static ItemRequest request;
    static User requester;
    static User otherUser;
    static Item item;
    static Pageable params = PageRequest.of(0, 1, Sort.by("id"));

    @BeforeEach
    void setUp() {
        requester = new User();
        requester.setName("User requester");
        requester.setEmail("requester.user@mail.com");
        requester = userRepository.save(requester);

        otherUser = new User();
        otherUser.setName("Potential sharer");
        otherUser.setEmail("sharer@mail.com");
        otherUser = userRepository.save(otherUser);

        request = new ItemRequest();
        request.setDescription("Describe item 1");
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());
        request = itemRequestRepository.save(request);

        item = new Item();
        item.setOwner(otherUser);
        item.setName("Item name");
        item.setDescription("Item description");
        item.setAvailable(true);
        item.setRequest(request);
        item = itemRepository.save(item);
    }
    @Test
    public void findByRequestIdListSuccess() {
        List<Item> checkList = itemRepository.findByRequestIdList(List.of(request.getId()));
        assertEquals(item, checkList.get(0));
    }

    @Test
    public void findItemsByTextIgnoreCaseSuccess() {
        List<Item> checkList = itemRepository.findItemsByTextIgnoreCase("nAme", params);
        assertEquals(item, checkList.get(0));
    }

}