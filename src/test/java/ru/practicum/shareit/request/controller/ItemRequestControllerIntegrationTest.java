package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoForResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.booking.controller.BookingController.HEADER;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mockMvc;
    private ItemRequestDtoForResponse itemRequestDtoForResponse;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    public void addItemRequests() {
        itemRequestDtoForResponse = new ItemRequestDtoForResponse();
        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("description");
    }

    @SneakyThrows
    @Test
    void createWhenItemRequestIsValidThenReturnedItemRequest() {
        Mockito.when(itemRequestService.create(Mockito.anyLong(), Mockito.any(ItemRequestDto.class)))
                .thenReturn(itemRequestDtoForResponse);

        String result = mockMvc.perform(post("/requests")
                        .header(HEADER, 1L)
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
        itemRequestDto.setDescription("");

        mockMvc.perform(post("/requests")
                        .header(HEADER, 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, Mockito.never()).create(Mockito.anyLong(), Mockito.any(ItemRequestDto.class));
    }

    @SneakyThrows
    @Test
    void getAllByRequestorWhenInvokedThenReturnedListOfItemRequests() {
        Mockito.when(itemRequestService.getAllByRequestor(Mockito.anyLong()))
                .thenReturn(List.of(itemRequestDtoForResponse));

        String result = mockMvc.perform(get("/requests")
                        .header(HEADER, 1L))
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
        Mockito.when(itemRequestService.getById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemRequestDtoForResponse);

        String result = mockMvc.perform(get("/requests/{id}", id)
                        .header(HEADER, 1L))
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
        Mockito.when(itemRequestService.getAll(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(itemRequestDtoForResponse));

        String result = mockMvc.perform(get("/requests/all")
                        .header(HEADER, 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(itemRequestDtoForResponse)), result);
        verify(itemRequestService).getAll(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
    }

}