package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
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
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDtoForResponse create(@RequestHeader(HEADER) Long userId,
                                            @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.create(userId, itemRequestDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDtoForResponse> getAllByRequestor(@RequestHeader(HEADER) Long userId) {
        return itemRequestService.getAllByRequestor(userId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDtoForResponse getById(@RequestHeader(HEADER) Long userId,
                                             @PathVariable Long id) {
        return itemRequestService.getById(userId, id);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDtoForResponse> getAll(@RequestHeader(HEADER) Long userId,
                                                  @RequestParam(defaultValue = "0") Integer from,
                                                  @RequestParam(defaultValue = "20") Integer size) {
        return itemRequestService.getAll(userId, from, size);
    }

}
