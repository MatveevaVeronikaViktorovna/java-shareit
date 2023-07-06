package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserMapper;
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
class ItemRequestServiceImplIntegrationTest {

    private final EntityManager em;
    private final ItemRequestService service;

    @Test
    void create() {
        User user1 = new User();
        user1.setName("name1");
        user1.setEmail("name1@yandex.ru");
        em.persist(user1);
        em.flush();

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("description");
        itemRequestDto.setRequestor(UserMapper.toDto(user1));

        service.create(user1.getId(), itemRequestDto);

        TypedQuery<ItemRequest> query = em.createQuery("Select ir from ItemRequest ir where ir.description = :description", ItemRequest.class);
        ItemRequest itemRequest = query
                .setParameter("description", itemRequestDto.getDescription())
                .getSingleResult();

        assertThat(itemRequest.getId(), notNullValue());
        assertThat(itemRequest.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequest.getRequestor().getId(), equalTo(itemRequestDto.getRequestor().getId()));
        assertThat(itemRequest.getCreated(), notNullValue());
    }
}