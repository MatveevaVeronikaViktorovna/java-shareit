package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.State;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
@Slf4j
public class BookingController {
    private final BookingClient bookingClient;
    public static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(HEADER) Long userId,
                                         @Valid @RequestBody BookingDto requestDto) {
        log.info("Поступил запрос на создание бронирования {} от пользователя с id={}", requestDto, userId);
        return bookingClient.create(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByBooker(@RequestHeader(HEADER) Long userId,
                                                 @RequestParam(defaultValue = "ALL") State state,
                                                 @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                 @RequestParam(defaultValue = "20") @Positive Integer size) {
        log.info("Поступил запрос на получение всех бронирований от пользователя с id={} со статусом {}, " +
                "from={}, size={}", userId, state, from, size);
        return bookingClient.getAllByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(@RequestHeader(HEADER) Long userId,
                                                @RequestParam(defaultValue = "ALL") State state,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                @RequestParam(defaultValue = "20") @Positive Integer size) {
        log.info("Поступил запрос на получение всех бронирований от пользователя(owner) с id={} со статусом {}, " +
                "from={}, size={}", userId, state, from, size);
        return bookingClient.getAllByOwner(userId, state, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@RequestHeader(HEADER) Long userId,
                                          @PathVariable Long id) {
        log.info("Поступил запрос на получение бронирования с id={} от пользователя с id={}", id, userId);
        return bookingClient.getById(userId, id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> approveOrReject(@RequestHeader(HEADER) Long userId,
                                                  @PathVariable Long id,
                                                  @RequestParam Boolean approved) {
        log.info("Поступил запрос на подтверждение/отклонение бронирования от пользователя с id={}, " +
                "бронирование с id={}, approved={}", id, userId, approved);
        return bookingClient.approveOrReject(userId, id, approved);
    }

}
