package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForResponse;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {

    private final BookingService bookingService;
    public static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDtoForResponse create(@RequestHeader(HEADER) Long userId,
                                        @RequestBody BookingDto bookingDto) {
        log.info("Поступил запрос на создание бронирования {} от пользователя с id={}", bookingDto, userId);
        return bookingService.create(userId, bookingDto);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDtoForResponse approveOrReject(@RequestHeader(HEADER) Long userId,
                                                 @PathVariable Long id,
                                                 @RequestParam Boolean approved) {
        log.info("Поступил запрос на подтверждение/отклонение бронирования от пользователя с id={}, " +
                "бронирование с id={}, approved={}", id, userId, approved);
        return bookingService.approveOrReject(userId, id, approved);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDtoForResponse getById(@RequestHeader(HEADER) Long userId,
                                         @PathVariable Long id) {
        log.info("Поступил запрос на получение бронирования с id={} от пользователя с id={}", id, userId);
        return bookingService.getById(userId, id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDtoForResponse> getAllByBooker(@RequestHeader(HEADER) Long userId,
                                                      @RequestParam(defaultValue = "ALL") State state,
                                                      @RequestParam(defaultValue = "0") Integer from,
                                                      @RequestParam(defaultValue = "20") Integer size) {
        log.info("Поступил запрос на получение всех бронирований от пользователя с id={} со статусом {}, " +
                "from={}, size={}", userId, state, from, size);
        return bookingService.getAllByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDtoForResponse> getAllByOwner(@RequestHeader(HEADER) Long userId,
                                                     @RequestParam(defaultValue = "ALL") State state,
                                                     @RequestParam(defaultValue = "0") Integer from,
                                                     @RequestParam(defaultValue = "20") Integer size) {
        log.info("Поступил запрос на получение всех бронирований от пользователя(owner) с id={} со статусом {}, " +
                "from={}, size={}", userId, state, from, size);

        return bookingService.getAllByOwner(userId, state, from, size);
    }

}
