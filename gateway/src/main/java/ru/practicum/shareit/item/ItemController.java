package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.Create;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.Update;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.booking.BookingController.HEADER;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/items")
@Validated
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(HEADER) Long userId,
                                         @Validated(Create.class) @RequestBody ItemDto requestDto) {
        log.info("Creating item {}, userId={}", requestDto, userId);
        return itemClient.create(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByOwner(@RequestHeader(HEADER) Long userId,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                @RequestParam(defaultValue = "20") @Positive Integer size) {
        log.info("Get item by owner userId={}, from={}, size={}", userId, from, size);
        return itemClient.getAllByOwner(userId, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@RequestHeader(HEADER) Long userId,
                                          @PathVariable Long id) {
        log.info("Get item {}, userId={}", id, userId);
        return itemClient.getById(userId, id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@RequestHeader(HEADER) Long userId,
                                         @PathVariable Long id,
                                         @Validated(Update.class) @RequestBody ItemDto requestDto) {
        log.info("Update item {}, userId={}, requestDto={}", id, userId, requestDto);
        return itemClient.update(userId, id, requestDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchByText(@RequestHeader(HEADER) Long userId,
                                               @RequestParam String text,
                                               @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                               @RequestParam(defaultValue = "20") @Positive Integer size) {
        return itemClient.searchByText(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(HEADER) Long userId,
                                             @PathVariable Long itemId,
                                             @Validated(Create.class) @RequestBody CommentDto commentDto) {
        return itemClient.addComment(userId, itemId, commentDto);
    }

}
