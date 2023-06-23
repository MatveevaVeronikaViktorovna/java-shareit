package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getAllWhenInvokedThenReturnedListOfUsers() {
        List<User> expectedUsers = List.of(new User());
        List<UserDto> expectedUsersDto = UserMapper.toDto(expectedUsers);
        Mockito.when(userRepository.findAll()).thenReturn(expectedUsers);

        List<UserDto> users = userService.getAll();

        assertEquals(expectedUsersDto, users);
    }

    @Test
    void getByIdWhenUserFoundThenReturnedUser() {
        Long id = 0L;
        User expectedUser = new User();
        UserDto expectedUserDto = UserMapper.toDto(expectedUser);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(expectedUser));

        UserDto user = userService.getById(id);

        assertEquals(expectedUserDto, user);

    }

    @Test
    void getByIdWhenUserNotFoundThenThrownException() {

    }

}