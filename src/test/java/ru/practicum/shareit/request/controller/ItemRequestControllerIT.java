package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoForResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerIT {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mockMvc;

    @SneakyThrows
    @Test
    void createWhenItemRequestIsValidThenReturnedItemRequest() {
        ItemRequestDtoForResponse itemRequestDtoForResponse = new ItemRequestDtoForResponse();
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1l);
        itemRequestDto.setDescription("description");
        Mockito.when(itemRequestService.create(Mockito.anyLong(), Mockito.any(ItemRequestDto.class))).thenReturn(itemRequestDtoForResponse);

        String result = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestDtoForResponse), result);
        verify(itemRequestService).create(Mockito.anyLong(), Mockito.any(ItemRequestDto.class));
    }

    @SneakyThrows
    @Test
    void createWhenItemRequestIsNotValidThenReturnedBadRequest() {
        ItemRequestDtoForResponse itemRequestDtoForResponse = new ItemRequestDtoForResponse();
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1l);
        itemRequestDto.setDescription("");

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, Mockito.never()).create(Mockito.anyLong(), Mockito.any(ItemRequestDto.class));
    }

    @SneakyThrows
    @Test
    void getAllByRequestorWhenInvokedThenReturnedListOfItemRequests() {
        ItemRequestDtoForResponse itemRequestDtoForResponse = new ItemRequestDtoForResponse();
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1l);
        itemRequestDto.setDescription("description");
        Mockito.when(itemRequestService.getAllByRequestor(Mockito.anyLong())).thenReturn(List.of(itemRequestDtoForResponse));

        String result = mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(itemRequestDtoForResponse)), result);
        verify(itemRequestService).getAllByRequestor(Mockito.anyLong());
    }

    @SneakyThrows
    @Test
    void getByIdWhenInvokedThenReturnedItemRequest() {
        Long id = 1L;
        ItemRequestDtoForResponse itemRequestDtoForResponse = new ItemRequestDtoForResponse();
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1l);
        itemRequestDto.setDescription("description");
        Mockito.when(itemRequestService.getById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(itemRequestDtoForResponse);

        String result = mockMvc.perform(get("/requests/{id}", id)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestDtoForResponse), result);
        verify(itemRequestService).getById(Mockito.anyLong(), Mockito.anyLong());
    }

    @SneakyThrows
    @Test
    void getAllWhenInvokedThenReturnedListOfItemRequests() {
        ItemRequestDtoForResponse itemRequestDtoForResponse = new ItemRequestDtoForResponse();
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1l);
        itemRequestDto.setDescription("description");
        Mockito.when(itemRequestService.getAll(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(List.of(itemRequestDtoForResponse));

        String result = mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(itemRequestDtoForResponse)), result);
        verify(itemRequestService).getAll(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
    }



}