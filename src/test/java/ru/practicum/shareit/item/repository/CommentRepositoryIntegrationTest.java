package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class CommentRepositoryIntegrationTest {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemRequestRepository itemRequestRepository;

    User user1;
    User user2;
    ItemRequest request1;
    ItemRequest request2;
    Item item1;
    Item item2;
    Comment comment1;
    Comment comment2;


    @BeforeEach
    public void addComments() {
        user1 = new User();
        user1.setName("name1");
        user1.setEmail("name1@yandex.ru");
        userRepository.save(user1);

        user2 = new User();
        user2.setName("name2");
        user2.setEmail("name2@yandex.ru");
        userRepository.save(user2);

        request1 = new ItemRequest();
        request1.setDescription("description1");
        request1.setRequestor(user1);
        request1.setCreated(LocalDateTime.now());
        itemRequestRepository.save(request1);

        request2 = new ItemRequest();
        request2.setDescription("description2");
        request2.setRequestor(user1);
        request2.setCreated(LocalDateTime.now());
        itemRequestRepository.save(request2);

        item1 = new Item();
        item1.setName("name1");
        item1.setDescription("description1");
        item1.setAvailable(true);
        item1.setOwner(user2);
        item1.setRequest(request1);
        itemRepository.save(item1);

        item2 = new Item();
        item2.setName("name2");
        item2.setDescription("description2");
        item2.setAvailable(true);
        item2.setOwner(user1);
        item2.setRequest(request2);
        itemRepository.save(item2);

        comment1 = new Comment();
        comment1.setText("comment1");
        comment1.setAuthor(user1);
        comment1.setItem(item2);
        comment1.setCreated(LocalDateTime.now());
        commentRepository.save(comment1);

        comment2 = new Comment();
        comment2.setText("comment2");
        comment2.setAuthor(user2);
        comment2.setItem(item1);
        comment2.setCreated(LocalDateTime.now());
        commentRepository.save(comment2);
    }

    @Test
    void findAllByItemId() {
        List<Comment> actualComments = commentRepository.findAllByItemId(item2.getId());

        assertEquals(1, actualComments.size());
        assertEquals("comment1", actualComments.get(0).getText());
    }

    @AfterEach
    public void deleteComments() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
        itemRepository.deleteAll();
        commentRepository.deleteAll();
    }

}