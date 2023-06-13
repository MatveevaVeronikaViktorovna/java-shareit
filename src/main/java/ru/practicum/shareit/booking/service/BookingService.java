package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForResponse;
import ru.practicum.shareit.item.dto.ItemDto;

public interface BookingService {

    BookingDtoForResponse create(Long userId, BookingDto bookingDto);
}
