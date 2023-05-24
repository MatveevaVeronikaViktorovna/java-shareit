package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDtoForCreate;
import ru.practicum.shareit.user.dto.UserDtoForUpdate;

import java.util.List;

public interface UserService {

    UserDtoForCreate create(UserDtoForCreate userDtoForCreate);

    List<UserDtoForCreate> getAll();

    UserDtoForCreate getById(Long id);

    UserDtoForCreate update(Long id, UserDtoForUpdate userDtoForUpdate);

    void delete(Long id);
}
