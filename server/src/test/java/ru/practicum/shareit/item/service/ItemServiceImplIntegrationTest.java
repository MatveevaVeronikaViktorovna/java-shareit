package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIntegrationTest {

    private final EntityManager em;
    private final ItemService service;

    @Test
    void create() {
        User user1 = new User();
        user1.setName("name1");
        user1.setEmail("name1@yandex.ru");
        em.persist(user1);
        em.flush();

        ItemDto item1 = new ItemDto();
        item1.setName("name1");
        item1.setDescription("description1");
        item1.setAvailable(true);

        service.create(user1.getId(), item1);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item item = query
                .setParameter("name", item1.getName())
                .getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(item1.getName()));
        assertThat(item.getDescription(), equalTo(item1.getDescription()));
        assertThat(item.getAvailable(), equalTo(item1.getAvailable()));
    }

}