package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForResponse;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDtoForResponse create(@RequestHeader(HEADER) Long userId,
                                        @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.create(userId, bookingDto);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDtoForResponse approveOrReject(@RequestHeader(HEADER) Long userId,
                                                 @PathVariable Long id,
                                                 @RequestParam Boolean approved) {
        return bookingService.approveOrReject(userId, id, approved);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDtoForResponse getById(@RequestHeader(HEADER) Long userId,
                                         @PathVariable Long id) {
        return bookingService.getById(userId, id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDtoForResponse> getAllByBooker(@RequestHeader(HEADER) Long userId,
                                                      @RequestParam(defaultValue = "ALL") State state) {
        return bookingService.getAllByBooker(userId, state);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDtoForResponse> getAllByOwner(@RequestHeader(HEADER) Long userId,
                                                     @RequestParam(defaultValue = "ALL") State state) {
        return bookingService.getAllByOwner(userId, state);
    }

}
