package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIntegrationTest {

    private final EntityManager em;
    private final UserService service;

    @Test
    void create() {
        UserDto userDto = new UserDto();
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
        userDto.setName("name");
        userDto.setEmail("name@yandex.ru");

        UserDto userDto2 = new UserDto();
        userDto2.setName("name2");
        userDto2.setEmail("name2@yandex.ru");

        List<UserDto> sourceUsers = List.of(userDto, userDto2);
        for (UserDto user : sourceUsers) {
            User entity = UserMapper.toUser(user);
            em.persist(entity);
        }
        em.flush();

        List<UserDto> targetUsers = service.getAll();

        assertThat(targetUsers, hasSize(sourceUsers.size()));
        for (UserDto sourceUser : sourceUsers) {
            assertThat(targetUsers, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceUser.getName())),
                    hasProperty("email", equalTo(sourceUser.getEmail()))
            )));
        }
    }

    @Test
    void getById() {
        UserDto userDto = new UserDto();
        userDto.setName("name");
        userDto.setEmail("name@yandex.ru");
        User sourceUser = UserMapper.toUser(userDto);
        em.persist(sourceUser);
        em.flush();

        UserDto targetUser = service.getById(sourceUser.getId());

        assertThat(targetUser.getId(), notNullValue());
        assertThat(targetUser.getName(), equalTo(userDto.getName()));
        assertThat(targetUser.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void update() {
        UserDto userDto = new UserDto();
        userDto.setName("name");
        userDto.setEmail("name@yandex.ru");
        User sourceUser = UserMapper.toUser(userDto);
        em.persist(sourceUser);
        em.flush();

        UserDto userDtoForUpdate = new UserDto();
        userDtoForUpdate.setName("name2");
        userDtoForUpdate.setEmail("name2@yandex.ru");

        UserDto targetUser = service.update(sourceUser.getId(), userDtoForUpdate);

        assertThat(targetUser.getId(), notNullValue());
        assertThat(targetUser.getName(), equalTo(userDtoForUpdate.getName()));
        assertThat(targetUser.getEmail(), equalTo(userDtoForUpdate.getEmail()));
    }

    @Test
    void delete() {
        UserDto userDto = new UserDto();
        userDto.setName("name");
        userDto.setEmail("name@yandex.ru");
        User sourceUser = UserMapper.toUser(userDto);
        em.persist(sourceUser);
        em.flush();

        service.delete(sourceUser.getId());

        TypedQuery<User> query = em.createQuery("Select u from User u", User.class);
        List<User> users = query
                .getResultList();

        assertThat(users.size(), equalTo(0));
    }

}