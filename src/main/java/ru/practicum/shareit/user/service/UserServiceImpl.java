package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User newUser = repository.save(user);
        log.info("Добавлен пользователь: {}", newUser);
        return UserMapper.toDto(newUser);
    }

    @Override
    public List<UserDto> getAll() {
        List<User> users = repository.findAll();
        List<UserDto> allUsers = new ArrayList<>();
        for (User user : users) {
            allUsers.add(UserMapper.toDto(user));
        }
        return allUsers;
    }

    @Override
    public UserDto getById(Long id) {
        Optional<User> user = repository.findById(id);
        if(user.isPresent()) {
            return UserMapper.toDto(user.get());
        } else {
            log.warn("Пользователь с id " + id + " не найден");
            throw new EntityNotFoundException(User.class.getSimpleName(), id);
        }
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
  /*      User user = UserMapper.toUser(userDto);
        User updatedUser = repository.update(id, user);
        log.info("Обновлен пользователь c id {} на {}", id, updatedUser);
        return UserMapper.toDto(updatedUser);
   */
        return null;
    }

    @Override
    public void delete(Long id) {
    /*    repository.delete(id);
        log.info("Удален пользователь с id {}", id);

     */
    }

}
