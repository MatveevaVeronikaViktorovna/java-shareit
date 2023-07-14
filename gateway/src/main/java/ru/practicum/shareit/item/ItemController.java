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
        log.info("Поступил запрос на создание вещи {} от пользователя с id={}", requestDto, userId);
        return itemClient.create(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByOwner(@RequestHeader(HEADER) Long userId,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                @RequestParam(defaultValue = "20") @Positive Integer size) {
        log.info("Поступил запрос на получение всех вещей от пользователя с id={}, from={}, size={}",
                userId, from, size);
        return itemClient.getAllByOwner(userId, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@RequestHeader(HEADER) Long userId,
                                          @PathVariable Long id) {
        log.info("Поступил запрос на получение вещи с id={} от пользователя с id={}", id, userId);
        return itemClient.getById(userId, id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@RequestHeader(HEADER) Long userId,
                                         @PathVariable Long id,
                                         @Validated(Update.class) @RequestBody ItemDto requestDto) {
        log.info("Поступил запрос на обновление вещи с id={} от пользователя с id={} на {}", id, userId, requestDto);
        return itemClient.update(userId, id, requestDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        log.info("Поступил запрос на удаление вещи с id={}", id);
        return itemClient.delete(id);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchByText(@RequestHeader(HEADER) Long userId,
                                               @RequestParam String text,
                                               @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                               @RequestParam(defaultValue = "20") @Positive Integer size) {
        log.info("Поступил запрос на поиск всех вещей по тексту от пользователя с id={}, text={}, from={}, size={}",
                userId, text, from, size);
        return itemClient.searchByText(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(HEADER) Long userId,
                                             @PathVariable Long itemId,
                                             @Validated(Create.class) @RequestBody CommentDto commentDto) {
        log.info("Поступил запрос на добавление комментария {} от пользователя с id={} к вещи с id={}",
                commentDto, userId, itemId);
        return itemClient.addComment(userId, itemId, commentDto);
    }

}
