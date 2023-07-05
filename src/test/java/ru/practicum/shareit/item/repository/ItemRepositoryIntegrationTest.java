package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@DataJpaTest
class ItemRepositoryIntegrationTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

/*    @BeforeEach
    public void addItems() {
        User owner1 = new User();
        owner1.setId(1L);
        owner1.setName("name1");
        owner1.setEmail("name1@yandex.ru");
        userRepository.save(owner1);

        User owner2 = new User();
        owner2.setId(2L);
        owner2.setName("name2");
        owner2.setEmail("name2@yandex.ru");
        userRepository.save(owner2);

        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("name1");
        item1.setDescription("description1");
        item1.setAvailable(true);
        item1.setOwner(owner1);
        ItemRequest request1 = new ItemRequest();
        request1.setId(1L);
        item1.setRequest(request1);
        itemRepository.save(item1);

        Item item2 = new Item();
        item1.setId(2L);
        item1.setName("name2");
        item1.setDescription("description2");
        item1.setAvailable(true);
        item2.setOwner(owner2);
        ItemRequest request2 = new ItemRequest();
        request2.setId(2L);
        item2.setRequest(request2);
        itemRepository.save(item2);
    } */

    @Test
    void findAllByOwnerIdOrderByIdAsc() {
     /*   Pageable page = PageRequest.of(0, 20);
        List<Item> actualItems = itemRepository.findAllByOwnerIdOrderByIdAsc(1L, page);

        assertEquals(1, actualItems.size()); */
    }

    @Test
    void findAllAvailableByText() {
    }

    @Test
    void findAllByRequestIdOrderByIdAsc() {
    }

    @AfterEach
    public void deleteItems(){
        itemRepository.deleteAll();
    }
}