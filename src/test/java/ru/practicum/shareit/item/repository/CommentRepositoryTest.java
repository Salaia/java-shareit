package ru.practicum.shareit.item.repository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class CommentRepositoryTest {
    @Autowired
    TestEntityManager entityManager;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    static User owner;
    static User booker;
    static Item item;
    static Comment comment;

    @BeforeEach
    public void setUp() {
        owner = new User();
        owner.setName("User Owner");
        owner.setEmail("owner.user@mail.com");
        owner = userRepository.save(owner);

        booker = new User();
        booker.setName("User Booker");
        booker.setEmail("booker.user@mail.com");
        booker = userRepository.save(booker);

        item = new Item();
        item.setOwner(owner);
        item.setName("Item name");
        item.setDescription("Item description");
        item.setAvailable(true);
        item = itemRepository.save(item);

        comment = new Comment();
        comment.setAuthor(booker);
        comment.setItem(item);
        comment.setCreated(Instant.now());
        comment.setText("Comment text");
        comment = commentRepository.save(comment);
    }

    @Test
    public void findByItemIdList() {
        List<Comment> checkList = commentRepository.findByItemIdList(List.of(item.getId()));
        assertEquals(comment, checkList.get(0));
    }
}