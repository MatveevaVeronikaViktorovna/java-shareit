package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Transactional
    @Override
    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User newUser = repository.save(user);
        log.info("Добавлен пользователь: {}", newUser);
        return UserMapper.toDto(newUser);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAll() {
        return repository.findAll()
                .stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getById(Long id) {
        User user = repository.findById(id).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", id);
            throw new EntityNotFoundException(String.format("Пользователь с id %d не найден", id));
        });
        return UserMapper.toDto(user);
    }

    @Transactional
    @Override
    public UserDto update(Long id, UserDto userDto) {
        User newUser = UserMapper.toUser(userDto);
        User oldUser = UserMapper.toUser(getById(id));
        newUser.setId(id);
        if (newUser.getName() == null) {
            newUser.setName(oldUser.getName());
        }
        if (newUser.getEmail() == null) {
            newUser.setEmail(oldUser.getEmail());
        }
        User updatedUser = repository.save(newUser);
        log.info("Обновлен пользователь c id {} на {}", id, updatedUser);
        return UserMapper.toDto(newUser);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        repository.deleteById(id);
        log.info("Удален пользователь с id {}", id);
    }

}
