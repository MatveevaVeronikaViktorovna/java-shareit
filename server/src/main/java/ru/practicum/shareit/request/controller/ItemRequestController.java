package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoForResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.booking.controller.BookingController.HEADER;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDtoForResponse create(@RequestHeader(HEADER) Long userId,
                                            @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Поступил запрос на создание запроса вещи {} от пользователя с id={}", itemRequestDto, userId);
        return itemRequestService.create(userId, itemRequestDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDtoForResponse> getAllByRequestor(@RequestHeader(HEADER) Long userId) {
        log.info("Поступил запрос на получение всех запросов вещей от пользователя с id={}", userId);
        return itemRequestService.getAllByRequestor(userId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDtoForResponse getById(@RequestHeader(HEADER) Long userId,
                                             @PathVariable Long id) {
        log.info("Поступил запрос на получение запроса вещи с id={} от пользователя с id={}", id, userId);
        return itemRequestService.getById(userId, id);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDtoForResponse> getAll(@RequestHeader(HEADER) Long userId,
                                                  @RequestParam(defaultValue = "0") Integer from,
                                                  @RequestParam(defaultValue = "20") Integer size) {
        log.info("Поступил запрос на получение всех запросов вещей от пользователя с id={}, from={}, size={}",
                userId, from, size);
        return itemRequestService.getAll(userId, from, size);
    }

}
