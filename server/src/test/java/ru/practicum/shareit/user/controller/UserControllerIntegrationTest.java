package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private UserDto userDto;

    @BeforeEach
    public void addUsers() {
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("name");
        userDto.setEmail("name@yandex.ru");
    }

    @SneakyThrows
    @Test
    void createWhenUserIsValidThenReturnedUser() {
        Mockito.when(userService.create(Mockito.any(UserDto.class))).thenReturn(userDto);

        String result = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);
        verify(userService).create(Mockito.any(UserDto.class));
    }

    @SneakyThrows
    @Test
    void getAllWhenInvokedThenReturnedListOfUsers() {
        Mockito.when(userService.getAll()).thenReturn(List.of(userDto));

        String result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(userDto)), result);
        verify(userService).getAll();
    }

    @SneakyThrows
    @Test
    void getByIdWhenInvokedThenReturnedUser() {
        Long userId = 1L;
        Mockito.when(userService.getById(Mockito.anyLong())).thenReturn(userDto);

        String result = mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);
        verify(userService).getById(userId);
    }

    @SneakyThrows
    @Test
    void updateWhenUserIsValidThenReturnedUpdatedUser() {
        Long userId = 1L;
        Mockito.when(userService.update(Mockito.anyLong(), Mockito.any(UserDto.class))).thenReturn(userDto);

        String result = mockMvc.perform(patch("/users/{id}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);
        verify(userService).update(userId, userDto);
    }

    @SneakyThrows
    @Test
    void deleteWhenInvokedThenReturnedOk() {
        Long userId = 1L;

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk());

        verify(userService).delete(Mockito.anyLong());
    }

}