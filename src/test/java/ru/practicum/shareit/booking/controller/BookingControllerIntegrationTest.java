package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForResponse;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.booking.controller.BookingController.HEADER;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;
    private BookingDtoForResponse bookingDtoForResponse;
    private BookingDto bookingDto;

    @BeforeEach
    public void addBookings() {
        bookingDtoForResponse = new BookingDtoForResponse();

        bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusDays(10L));
        bookingDto.setEnd(LocalDateTime.now().plusDays(20L));
        bookingDto.setItemId(1L);
    }

    @SneakyThrows
    @Test
    void createWhenBookingIsValidThenReturnedBooking() {
        Mockito.when(bookingService.create(Mockito.anyLong(), Mockito.any(BookingDto.class)))
                .thenReturn(bookingDtoForResponse);

        String result = mockMvc.perform(post("/bookings")
                        .header(HEADER, 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDtoForResponse), result);
        verify(bookingService).create(Mockito.anyLong(), Mockito.any(BookingDto.class));
    }

    @SneakyThrows
    @Test
    void createWhenBookingIsNotValidThenReturnedBadRequest() {
        bookingDto.setStart(LocalDateTime.now().minusDays(10L));

        mockMvc.perform(post("/bookings")
                        .header(HEADER, 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());

        verify(bookingService, Mockito.never()).create(Mockito.anyLong(), Mockito.any(BookingDto.class));
    }

    @SneakyThrows
    @Test
    void approveWhenInvokedThenReturnedBooking() {
        Long id = 1L;
        Mockito.when(bookingService.approveOrReject(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean()))
                .thenReturn(bookingDtoForResponse);

        String result = mockMvc.perform(patch("/bookings/{id}", id)
                        .header(HEADER, 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDtoForResponse), result);
        verify(bookingService).approveOrReject(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean());
    }

    @SneakyThrows
    @Test
    void getByIdWhenInvokedThenReturnedBooking() {
        Long id = 1L;
        Mockito.when(bookingService.getById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(bookingDtoForResponse);

        String result = mockMvc.perform(get("/bookings/{id}", id)
                        .header(HEADER, 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDtoForResponse), result);
        verify(bookingService).getById(Mockito.anyLong(), Mockito.anyLong());
    }

    @SneakyThrows
    @Test
    void getAllByBookerWhenInvokedThenReturnedListOfBooking() {
        Mockito.when(bookingService.getAllByBooker(Mockito.anyLong(), Mockito.any(State.class), Mockito.anyInt(),
                Mockito.anyInt())).thenReturn(List.of(bookingDtoForResponse));

        String result = mockMvc.perform(get("/bookings")
                        .header(HEADER, 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(bookingDtoForResponse)), result);
        verify(bookingService).getAllByBooker(Mockito.anyLong(), Mockito.any(State.class), Mockito.anyInt(),
                Mockito.anyInt());
    }

    @SneakyThrows
    @Test
    void getAllByOwnerWhenInvokedThenReturnedListOfBooking() {
        Mockito.when(bookingService.getAllByOwner(Mockito.anyLong(), Mockito.any(State.class), Mockito.anyInt(),
                Mockito.anyInt())).thenReturn(List.of(bookingDtoForResponse));

        String result = mockMvc.perform(get("/bookings/owner")
                        .header(HEADER, 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(bookingDtoForResponse)), result);
        verify(bookingService).getAllByOwner(Mockito.anyLong(), Mockito.any(State.class), Mockito.anyInt(),
                Mockito.anyInt());
    }

}