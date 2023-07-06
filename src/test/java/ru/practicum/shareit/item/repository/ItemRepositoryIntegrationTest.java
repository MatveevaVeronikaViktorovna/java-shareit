package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryIntegrationTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRequestRepository itemRequestRepository;

    Pageable page = PageRequest.of(0, 20);
    User user1;
    User user2;
    ItemRequest request1;
    ItemRequest request2;
    Item item1;
    Item item2;

    @BeforeEach
    public void addItems() {
        user1 = new User();
        user1.setName("name1");
        user1.setEmail("name1@yandex.ru");
        userRepository.save(user1);

        request1 = new ItemRequest();
        request1.setDescription("description1");
        request1.setRequestor(user1);
        request1.setCreated(LocalDateTime.now());
        itemRequestRepository.save(request1);

        user2 = new User();
        user2.setName("name2");
        user2.setEmail("name2@yandex.ru");
        userRepository.save(user2);

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
    }

    @Test
    void findAllByOwnerIdOrderByIdAsc() {
        List<Item> actualItems = itemRepository.findAllByOwnerIdOrderByIdAsc(user1.getId(), page);

        assertEquals(1, actualItems.size());
        assertEquals("name2", actualItems.get(0).getName());
    }

    @Test
    void findAllAvailableByText() {
        List<Item> actualItems = itemRepository.findAllAvailableByText("name", page);

        assertEquals(2, actualItems.size());
        assertEquals("name1", actualItems.get(0).getName());
        assertEquals("name2", actualItems.get(1).getName());
    }

    @Test
    void findAllByRequestIdOrderByIdAsc() {
        List<Item> actualItems = itemRepository.findAllByRequestIdOrderByIdAsc(user1.getId());

        assertEquals(1, actualItems.size());
        assertEquals("name1", actualItems.get(0).getName());
    }

    @AfterEach
    public void deleteItems() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
        itemRepository.deleteAll();
    }
}