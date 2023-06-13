package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.user.dto.Create;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDtoForResponse create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @Validated(Create.class) @RequestBody BookingDto bookingDto) {
        return bookingService.create(userId, bookingDto);
    }

}
