package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
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

    private final UserDtoMapper mapper = Mappers.getMapper(UserDtoMapper.class);

    private Long id;
    private User expectedUser;
    private UserDto expectedUserDto;
    private User userForUpdate;
    private UserDto userForUpdateDto;

    @BeforeEach
    public void addUsers() {
        id = 0L;
        expectedUser = new User();
        expectedUser.setName("name");
        expectedUser.setEmail("name@yandex.ru");
        expectedUserDto = mapper.userToDto(expectedUser);

        userForUpdate = new User();
        userForUpdateDto = mapper.userToDto(userForUpdate);
    }

    @Test
    void createUserWhenUserValidThenSavedUser() {
        Mockito.when(userRepository.save(expectedUser)).thenReturn(expectedUser);

        UserDto user = userService.create(expectedUserDto);

        assertEquals(expectedUserDto, user);
        verify(userRepository).save(expectedUser);
    }

    @Test
    void createUserWhenEmailAlreadyExistThenNotSavedUser() {
        Mockito.when(userRepository.save(expectedUser)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(DataIntegrityViolationException.class, () -> userService.create(expectedUserDto));
    }

    @Test
    void getAllWhenInvokedThenReturnedListOfUsers() {
        List<User> expectedUsers = List.of(new User());
        List<UserDto> expectedUsersDto = new ArrayList<>();
        for (User user : expectedUsers) {
            expectedUsersDto.add(mapper.userToDto(user));
        }
        Mockito.when(userRepository.findAll()).thenReturn(expectedUsers);

        List<UserDto> users = userService.getAll();

        assertEquals(expectedUsersDto, users);
    }

    @Test
    void getByIdWhenUserFoundThenReturnedUser() {
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(expectedUser));

        UserDto user = userService.getById(id);

        assertEquals(expectedUserDto, user);
    }

    @Test
    void getByIdWhenUserNotFoundThenThrownException() {
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());

        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService
                .getById(id));
        assertEquals("Пользователь с id 0 не найден", exception.getMessage());
    }

    @Test
    void updateWhenUserFoundThenUserUpdated() {
        userForUpdate.setName("updatedName");
        userForUpdate.setEmail("updatedName@yandex.ru");
        userForUpdateDto = mapper.userToDto(userForUpdate);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(expectedUser));

        userService.update(id, userForUpdateDto);

        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();
        assertEquals("updatedName", savedUser.getName());
        assertEquals("updatedName@yandex.ru", savedUser.getEmail());
    }

    @Test
    void updateUserNameWhenUserFoundThenUpdatedOnlyName() {
        userForUpdate.setName("updatedName");
        userForUpdateDto = mapper.userToDto(userForUpdate);

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(expectedUser));

        userService.update(id, userForUpdateDto);

        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();
        System.out.println(savedUser);
        assertEquals("updatedName", savedUser.getName());
        assertEquals("name@yandex.ru", savedUser.getEmail());
    }

    @Test
    void updateUserEmailWhenUserFoundThenUpdatedOnlyEmail() {
        userForUpdate.setEmail("updatedName@yandex.ru");
        userForUpdateDto = mapper.userToDto(userForUpdate);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(expectedUser));

        userService.update(id, userForUpdateDto);

        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();
        assertEquals("name", savedUser.getName());
        assertEquals("updatedName@yandex.ru", savedUser.getEmail());
    }

    @Test
    void updateUserEmailWhenUserFoundAndEmailAlreadyExistThenNotUpdated() {
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(expectedUser));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(DataIntegrityViolationException.class, () -> userService.update(id, userForUpdateDto));
    }

    @Test
    void updateWhenUserNotFoundThenUserNotUpdated() {
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.update(id, userForUpdateDto));
        verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    void deleteWhenInvokedThenDeleteUser() {
        userService.delete(id);
        verify(userRepository, Mockito.times(1)).deleteById(id);
    }

}