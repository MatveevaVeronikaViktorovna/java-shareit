package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.UserAlreadyExistException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryInMemoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private long newId;

    @Override
    public User create(User user) {
        if (isEmailAlreadyExist(user.getEmail())) {
            log.warn("Пользователь с email " + user.getEmail() + " уже существует.");
            throw new UserAlreadyExistException("Пользователь с email " + user.getEmail() + " уже существует.");
        }
        newId++;
        user.setId(newId);
        users.put(newId, user);
        return user;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(Long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            log.warn("Пользователь с id " + id + " не найден");
            throw new EntityNotFoundException(User.class.getSimpleName(), id);
        }
    }

    @Override
    public User update(Long id, User user) {
        User oldUser = users.get(id);
        user.setId(id);
        if (user.getName() == null) {
            user.setName(oldUser.getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(oldUser.getEmail());
        } else {
            if (isEmailAlreadyExist(user.getEmail()) && !oldUser.getEmail().equals(user.getEmail())) {
                log.warn("Пользователь с email " + user.getEmail() + " уже существует.");
                throw new UserAlreadyExistException("Пользователь с email " + user.getEmail() + " уже существует.");
            }
        }
        users.put(id, user);
        return user;
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }

    private boolean isEmailAlreadyExist(String email) {
        for (User user : users.values()) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

}
