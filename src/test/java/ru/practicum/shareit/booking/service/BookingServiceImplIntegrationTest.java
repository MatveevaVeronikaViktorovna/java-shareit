package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIntegrationTest {

    private final EntityManager em;
    private final BookingService service;

    @Test
    void create() {
        User user1 = new User();
        user1.setName("name1");
        user1.setEmail("name1@yandex.ru");
        em.persist(user1);
        em.flush();

        User user2 = new User();
        user2.setName("name2");
        user2.setEmail("name2@yandex.ru");
        em.persist(user2);
        em.flush();

        Item item1 = new Item();
        item1.setName("name1");
        item1.setDescription("description1");
        item1.setAvailable(true);
        item1.setOwner(user1);
        em.persist(item1);
        em.flush();

        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusDays(1L));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2L));
        bookingDto.setItemId(item1.getId());

        service.create(user2.getId(), bookingDto);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.item = :item", Booking.class);
        Booking booking = query
                .setParameter("item", item1)
                .getSingleResult();

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStart(), equalTo(bookingDto.getStart()));
        assertThat(booking.getEnd(), equalTo(bookingDto.getEnd()));
    }
}