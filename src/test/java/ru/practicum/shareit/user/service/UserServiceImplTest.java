package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Test
    void createUserWhenUserValidThenSavedUser() {
        User expectedUser = new User();
        UserDto expectedUserDto = UserMapper.toDto(expectedUser);
        Mockito.when(userRepository.save(expectedUser)).thenReturn(expectedUser);

        UserDto user = userService.create(expectedUserDto);

        assertEquals(expectedUserDto, user);
        verify(userRepository).save(expectedUser);
    }

    @Test
    void createUserWhenEmailAlreadyExistThenNotSavedUser() {
        User expectedUser = new User();
        UserDto expectedUserDto = UserMapper.toDto(expectedUser);
        Mockito.when(userRepository.save(expectedUser)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(DataIntegrityViolationException.class, () -> userService.create(expectedUserDto));
    }

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
        Long id = 0L;
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());

        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService
                .getById(id));
        assertEquals("Пользователь с id 0 не найден", exception.getMessage());
    }

    @Test
    void updateWhenUserFoundThenUserUpdated() {
        Long id = 0L;
        User oldUser = new User();
        oldUser.setName("name");
        oldUser.setEmail("name@yandex.ru");

        User newUser = new User();
        newUser.setName("updatedName");
        newUser.setEmail("updatedName@yandex.ru");
        UserDto newUserDto = UserMapper.toDto(newUser);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(oldUser));

        userService.update(id, newUserDto);

        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();
        assertEquals("updatedName", savedUser.getName());
        assertEquals("updatedName@yandex.ru", savedUser.getEmail());
    }

    @Test
    void updateUserNameWhenUserFoundThenUpdatedOnlyName() {
        Long id = 0L;
        User oldUser = new User();
        oldUser.setName("name");
        oldUser.setEmail("name@yandex.ru");

        User newUser = new User();
        newUser.setName("updatedName");
        UserDto newUserDto = UserMapper.toDto(newUser);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(oldUser));

        userService.update(id, newUserDto);

        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();
        System.out.println(savedUser);
        assertEquals("updatedName", savedUser.getName());
        assertEquals("name@yandex.ru", savedUser.getEmail());
    }

    @Test
    void updateUserEmailWhenUserFoundThenUpdatedOnlyEmail() {
        Long id = 0L;
        User oldUser = new User();
        oldUser.setName("name");
        oldUser.setEmail("name@yandex.ru");

        User newUser = new User();
        newUser.setEmail("updatedName@yandex.ru");
        UserDto newUserDto = UserMapper.toDto(newUser);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(oldUser));

        userService.update(id, newUserDto);

        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();
        assertEquals("name", savedUser.getName());
        assertEquals("updatedName@yandex.ru", savedUser.getEmail());
    }

    @Test
    void updateUserEmailWhenUserFoundAndEmailAlreadyExistThenNotUpdated() {
        Long id = 0L;
        User oldUser = new User();
        User newUser = new User();
        UserDto newUserDto = UserMapper.toDto(newUser);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(oldUser));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(DataIntegrityViolationException.class, () -> userService.update(id, newUserDto));
    }

    @Test
    void updateWhenUserNotFoundThenUserNotUpdated() {
        Long id = 0L;
        User newUser = new User();
        UserDto newUserDto = UserMapper.toDto(newUser);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.update(id, newUserDto));
        verify(userRepository, Mockito.never()).save(newUser);
    }

    @Test
    void deleteWhenInvokedThenDeleteUser() {
        Long id = 0L;
        userService.delete(id);
        verify(userRepository, Mockito.times(1)).deleteById(id);
    }

}