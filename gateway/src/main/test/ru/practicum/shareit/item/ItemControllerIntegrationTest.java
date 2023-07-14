package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.booking.BookingController.HEADER;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    private ItemDto itemDto;
    private CommentDto commentDto;

    @SneakyThrows
    @Test
    void createWhenItemIsNotValidThenReturnedBadRequest() {
        itemDto.setName("");

        mockMvc.perform(post("/items")
                        .header(HEADER, 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, Mockito.never()).create(Mockito.anyLong(), Mockito.any(ItemDto.class));
    }

    @SneakyThrows
    @Test
    void addCommentWhenCommentNotValidThenReturnedBadRequest() {
        Long itemId = 1L;
        commentDto.setText("");

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(HEADER, 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, Mockito.never()).addComment(Mockito.anyLong(), Mockito.anyLong(),
                Mockito.any(CommentDto.class));
    }

}