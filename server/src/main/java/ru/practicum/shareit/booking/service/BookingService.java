package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.controller.State;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForResponse;

import java.util.List;

public interface BookingService {

    BookingDtoForResponse create(Long userId, BookingDto bookingDto);

    BookingDtoForResponse approveOrReject(Long userId, Long id, Boolean approved);

    BookingDtoForResponse getById(Long userId, Long id);

    List<BookingDtoForResponse> getAllByBooker(Long userId, State state, Integer from, Integer size);

    List<BookingDtoForResponse> getAllByOwner(Long userId, State state, Integer from, Integer size);
}