package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.booking.BookingController.HEADER;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mockMvc;
    private ItemRequestDto itemRequestDto;

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

}