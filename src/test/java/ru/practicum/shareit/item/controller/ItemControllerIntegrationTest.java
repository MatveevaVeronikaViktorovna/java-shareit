package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    @SneakyThrows
    @Test
    void createWhenItemIsValidThenReturnedItem() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        Mockito.when(itemService.create(Mockito.anyLong(), Mockito.any(ItemDto.class))).thenReturn(itemDto);

        String result = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
        verify(itemService).create(Mockito.anyLong(), Mockito.any(ItemDto.class));
    }

    @SneakyThrows
    @Test
    void createWhenItemIsNotValidThenReturnedBadRequest() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, Mockito.never()).create(Mockito.anyLong(), Mockito.any(ItemDto.class));
    }

    @SneakyThrows
    @Test
    void createWithoutHeaderThenReturnedBadRequest() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);

        mockMvc.perform(post("/items")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, Mockito.never()).create(Mockito.anyLong(), Mockito.any(ItemDto.class));
    }

    @SneakyThrows
    @Test
    void getAllByOwnerWhenInvokedThenReturnedListOfItems() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        Mockito.when(itemService.getAllByOwner(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(itemDto));

        String result = mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(itemDto)), result);
        verify(itemService).getAllByOwner(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
    }

    @SneakyThrows
    @Test
    void getByIdWhenInvokedThenReturnedItem() {
        Long itemId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        Mockito.when(itemService.getById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(itemDto);

        String result = mockMvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
        verify(itemService).getById(Mockito.anyLong(), Mockito.anyLong());
    }

    @SneakyThrows
    @Test
    void updateWhenItemIsValidThenReturnedUpdatedItem() {
        Long itemId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        Mockito.when(itemService.update(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(ItemDto.class)))
                .thenReturn(itemDto);

        String result = mockMvc.perform(patch("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
        verify(itemService).update(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(ItemDto.class));
    }

    @SneakyThrows
    @Test
    void deleteWhenInvokedThenReturnedOk() {
        Long itemId = 1L;

        mockMvc.perform(delete("/items/{id}", itemId))
                .andExpect(status().isOk());

        verify(itemService).delete(Mockito.anyLong());
    }

    @SneakyThrows
    @Test
    void searchByTextWhenInvokedThenReturnedListOfItems() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        Mockito.when(itemService.searchByText(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(),
                Mockito.anyInt())).thenReturn(List.of(itemDto));

        String result = mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "name"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(itemDto)), result);
        verify(itemService).searchByText(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt());

    }

    @SneakyThrows
    @Test
    void addCommentWhenCommentIsValidThenReturnedComment() {
        Long itemId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("text");
        Mockito.when(itemService.addComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(CommentDto.class)))
                .thenReturn(commentDto);

        String result = mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(commentDto), result);
        verify(itemService).addComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(CommentDto.class));
    }

    @SneakyThrows
    @Test
    void addCommentWhenCommentNotValidThenReturnedBadRequest() {
        Long itemId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("");

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, Mockito.never()).addComment(Mockito.anyLong(), Mockito.anyLong(),
                Mockito.any(CommentDto.class));
    }

}