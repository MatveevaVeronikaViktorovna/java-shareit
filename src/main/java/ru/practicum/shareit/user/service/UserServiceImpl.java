package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDtoForCreate;
import ru.practicum.shareit.user.dto.UserDtoForUpdate;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public UserDtoForCreate create(UserDtoForCreate userDtoForCreate) {
        User user = UserMapper.convertDtoForCreateToUser(userDtoForCreate);
        User newUser = repository.create(user);
        log.info("Добавлен пользователь: {}", newUser);
        return UserMapper.convertUserToDto(newUser);
    }

    @Override
    public List<UserDtoForCreate> getAll() {
        List<User> users = repository.getAll();
        List<UserDtoForCreate> allUsers = new ArrayList<>();
        for (User user : users) {
            allUsers.add(UserMapper.convertUserToDto(user));
        }
        return allUsers;
    }

    @Override
    public UserDtoForCreate getById(Long id) {
        User user = repository.getById(id);
        return UserMapper.convertUserToDto(user);
    }

    @Override
    public UserDtoForCreate update(Long id, UserDtoForUpdate userDtoForUpdate) {
        User user = UserMapper.convertDtoForUpdateToUser(userDtoForUpdate);
        User updatedUser = repository.update(id, user);
        log.info("Обновлен пользователь c id {} на {}", id, updatedUser);
        return UserMapper.convertUserToDto(updatedUser);
    }

    @Override
    public void delete(Long id) {
        repository.delete(id);
        log.info("Удален пользователь с id {}", id);
    }

}
