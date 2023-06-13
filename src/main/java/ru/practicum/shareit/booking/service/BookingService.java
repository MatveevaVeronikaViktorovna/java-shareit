package ru.practicum.shareit.booking.service;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForResponse;

public interface BookingService {

    BookingDtoForResponse create(Long userId, BookingDto bookingDto);

    BookingDtoForResponse approveOrReject(Long userId, Long id, Boolean approved);
    BookingDtoForResponse getById(Long userId, Long id);

}