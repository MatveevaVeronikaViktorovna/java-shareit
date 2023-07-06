package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIntegrationTest {

    private final EntityManager em;
    private final UserService service;

    @Test
    void create() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("name");
        userDto.setEmail("name@yandex.ru");

        service.create(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query
                .setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void getAll() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("name");
        userDto.setEmail("name@yandex.ru");

        UserDto userDto2 = new UserDto();
        userDto2.setId(2L);
        userDto2.setName("name2");
        userDto2.setEmail("name2@yandex.ru");

        service.create(userDto);
        service.create(userDto2);

        TypedQuery<User> query = em.createQuery("Select u from User u", User.class);
        List<User> users = query
                .getResultList();

        assertThat(users.size(), equalTo(2));
        assertThat(users.get(0).getId(), notNullValue());
        assertThat(users.get(0).getName(), equalTo(userDto.getName()));
        assertThat(users.get(0).getEmail(), equalTo(userDto.getEmail()));
        assertThat(users.get(1).getId(), notNullValue());
        assertThat(users.get(1).getName(), equalTo(userDto2.getName()));
        assertThat(users.get(1).getEmail(), equalTo(userDto2.getEmail()));
    }

    @Test
    void getById() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("name");
        userDto.setEmail("name@yandex.ru");

        UserDto userDto2 = new UserDto();
        userDto2.setId(2L);
        userDto2.setName("name2");
        userDto2.setEmail("name2@yandex.ru");

        service.create(userDto);
        service.create(userDto2);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query
                .setParameter("id", userDto.getId())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void update() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("name");
        userDto.setEmail("name@yandex.ru");

        UserDto userDtoForUpdate = new UserDto();
        userDtoForUpdate.setName("name2");
        userDtoForUpdate.setEmail("name2@yandex.ru");

        service.create(userDto);
        service.update(userDto.getId(), userDtoForUpdate);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query
                .setParameter("id", userDto.getId())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDtoForUpdate.getName()));
        assertThat(user.getEmail(), equalTo(userDtoForUpdate.getEmail()));
    }

    @Test
    void delete() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("name");
        userDto.setEmail("name@yandex.ru");

        UserDto userDto2 = new UserDto();
        userDto2.setId(2L);
        userDto2.setName("name2");
        userDto2.setEmail("name2@yandex.ru");

        service.create(userDto);
        service.create(userDto2);
        service.delete(userDto.getId());

        TypedQuery<User> query = em.createQuery("Select u from User u", User.class);
        List<User> users = query
                .getResultList();

        assertThat(users.size(), equalTo(1));
        assertThat(users.get(0).getId(), notNullValue());
        assertThat(users.get(0).getName(), equalTo(userDto2.getName()));
        assertThat(users.get(0).getEmail(), equalTo(userDto2.getEmail()));
    }

}