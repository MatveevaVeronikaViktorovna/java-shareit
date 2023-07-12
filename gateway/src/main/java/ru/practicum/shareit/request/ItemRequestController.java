package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.Create;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.booking.BookingController.HEADER;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Validated
@Slf4j
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(HEADER) Long userId,
                                         @Validated(Create.class) @RequestBody ItemRequestDto requestDto) {
        return itemRequestClient.create(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByRequestor(@RequestHeader(HEADER) Long userId) {
        return itemRequestClient.getAllByRequestor(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@RequestHeader(HEADER) Long userId,
                                          @PathVariable Long id) {
        return itemRequestClient.getById(userId, id);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader(HEADER) Long userId,
                                         @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                         @RequestParam(defaultValue = "20") @Positive Integer size) {
        return itemRequestClient.getAll(userId, from, size);
    }

}

