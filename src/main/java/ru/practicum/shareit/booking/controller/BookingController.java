package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.Create;

import java.util.List;

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

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDtoForResponse approveOrReject(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long id,
                                                 @RequestParam Boolean approved) {
        return bookingService.approveOrReject(userId, id, approved);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDtoForResponse getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long id) {
        return bookingService.getById(userId, id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDtoForResponse> getAllByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam(defaultValue = "ALL") State state) {
        return bookingService.getAllByBooker(userId, state);
    }
}
